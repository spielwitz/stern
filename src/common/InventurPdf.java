/**	STERN, das Strategiespiel.
    Copyright (C) 1989-2020 Michael Schweitzer, spielwitz@icloud.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>. **/

package common;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.ImgTemplate;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import commonUi.PaintPanel;

public class InventurPdf extends PdfPageEventHelper
{
	private ByteArrayOutputStream outputStream;
	private Document document;
	private PdfWriter writer;
	private InventurPdfDaten daten;
	
	private com.itextpdf.text.Font chapterFont;
	private com.itextpdf.text.Font paragraphFont;
	private com.itextpdf.text.Font linkFont;
	private float charWidth;
		
    // store the chapters and sections with their title here.
    private final Map<String, Integer>     pageByTitle    = new HashMap<>();
    
	public static byte[] create(InventurPdfDaten daten) throws Exception
	{
		return new InventurPdf(daten).outputStream.toByteArray();
	}
	
	private InventurPdf(InventurPdfDaten daten) throws Exception
	{
		this.daten = daten;
		
		float fontSizeParagraph = this.daten.nurSpielfeld ? 6F : 8F;
		
		this.chapterFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 12, Font.BOLD);
		this.paragraphFont = FontFactory.getFont(FontFactory.COURIER, fontSizeParagraph, Font.PLAIN);
		this.linkFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.PLAIN, BaseColor.BLUE);
		
		this.outputStream = new ByteArrayOutputStream();
		this.document = new Document();
		this.writer = PdfWriter.getInstance(document, outputStream);
		this.writer.setPageEvent(this);
		this.document.open();
		
		this.charWidth = this.paragraphFont.getBaseFont().getWidthPoint("H", fontSizeParagraph);
		
		// Titelseite
		if (!this.daten.nurSpielfeld)
			this.addCover();
		
		// Kapitel
		for (int i = 0; i < this.daten.chapters.size(); i++)
			this.addChapter(i);
		
		document.close();
	}
	
	private void addChapter(int i) throws Exception
	{
		// append the chapter
		InventurPdfChapter c = this.daten.chapters.get(i);
		
        final String title = c.chapterName;
        Chunk chunk = new Chunk(title, this.chapterFont).setLocalDestination(title);
        Paragraph p1 = new Paragraph(chunk);
        p1.setAlignment(Element.ALIGN_CENTER);
        final Chapter chapter = new Chapter(p1, i);
        chapter.add(Chunk.NEWLINE);
        chapter.setNumberDepth(0);

        this.document.add(chapter);
        
        // Grafik
        if (c.sdc != null)
        {
	        this.addGraphic(c.sdc);
	        document.add( Chunk.NEWLINE );
        }
        
		// Tabelle 
        if (c.table != null)
			this.addTable(c.table);
        else
        {
        	chunk = new Chunk(c.nichtVorhandenMsg, this.paragraphFont).setLocalDestination(title);
            p1 = new Paragraph(chunk);
            p1.setAlignment(Element.ALIGN_CENTER);
            this.document.add(p1);
        }
   	}
	
	private void addTable(InventurPdfTable tableData) throws Exception
	{
		// Maximale Breite der Spalten ermitteln
		float[] colWidth = new float[tableData.colAlignRight.length];
		
		for (int i = 0; i < tableData.cells.size(); i++)
		{
			int col = i % tableData.colAlignRight.length;
			
			String cellText = tableData.cells.get(i);
			String[] cellTextLines = cellText.split("\n");
			
			for (int j = 0; j < cellTextLines.length; j++)
			{
				float width = (float)(cellTextLines[j].length() + 1) * this.charWidth;
				if (width > colWidth[col])
					colWidth[col] = width;
			}
		}
		
		// Tabelle aufbauen
		PdfPTable table = new PdfPTable(colWidth.length);
        table.setTotalWidth(colWidth);
        table.setLockedWidth(true);
        //table.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.setHeaderRows(1);
        
        for (int i = 0; i < tableData.cells.size(); i++)
		{
			int row = i / tableData.colAlignRight.length;
			int col = i % tableData.colAlignRight.length;
			
			PdfPCell cell = new PdfPCell(new Phrase(tableData.cells.get(i), this.paragraphFont));
			cell.setHorizontalAlignment(
					(row == 0 || !tableData.colAlignRight[col]) ?
							Element.ALIGN_LEFT :
							Element.ALIGN_RIGHT);
			
			cell.setNoWrap(true);
			
			if (row == 0 || (tableData.highlightFirstCol && col == 0))
			{
				cell.setBackgroundColor(GrayColor.LIGHT_GRAY);
			}
			table.addCell(cell);
		}
        
        document.add(table);
	}
	
	private void addGraphic(ScreenDisplayContent sdc) throws Exception
	{
        Rectangle size = document.getPageSize();
		int width = (int)(size.getWidth() - document.leftMargin() - document.rightMargin());		
		
		if (this.daten.nurSpielfeld)
			width = (int)(width * 0.45);
		
		double factor = this.daten.nurSpielfeld ? 
				(double)(width+ 2 * PaintPanel.RAND) / (double)(PaintPanel.SCREEN_SIZE_W - 220) :
				(double)(width+ 2 * PaintPanel.RAND) / (double)(PaintPanel.SCREEN_SIZE_W);
		
		// Ohne Console-Bereich
		int height = (int)((double)(Constants.FELD_MAX_Y * ScreenPainter.SPIELFELD_DX+ 2 * PaintPanel.RAND) * factor);
        
        PdfContentByte canvas = writer.getDirectContent();
        PdfTemplate template = canvas.createTemplate(width, height);
        Graphics2D g2d = new PdfGraphics2D(template, width, height);
        
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, width, height);
        
        Font fontPlaneten = new Font(Font.MONOSPACED, Font.PLAIN, Utils.round((double)PaintPanel.FONT_SIZE_PLANETEN * factor));
        Font fontMinen = new Font(Font.MONOSPACED, Font.PLAIN, Utils.round((double)PaintPanel.FONT_SIZE_MINEN * factor));
        Font fontFelder = new Font(Font.MONOSPACED, Font.PLAIN, Utils.round((double)PaintPanel.FONT_SIZE_FELDER * factor));
        
        if (!this.daten.nurSpielfeld)
        {
	        AffineTransform trans = new AffineTransform();
	        	trans.setToScale(0.92, 0.92);
	        trans.translate((double)PaintPanel.RAND / factor, (double)PaintPanel.RAND / factor);
	        g2d.setTransform(trans);
        }
                        
        new ScreenPainter(sdc, true, g2d, fontPlaneten, fontMinen, fontFelder, factor);
        
        g2d.dispose();
        
        ImgTemplate img = new ImgTemplate(template);
        img.setAlignment(Element.ALIGN_CENTER);
        document.add(img);

	}
	
	public static String formatDate (long time)
	{
		return time > 0 ? DateFormat.getDateTimeInstance().format(new Date(time)) : "";
	}
	
	private void addCover() throws Exception
    {
        Chunk chunk = new Chunk(SternResources.InventurTitel(false), chapterFont);
        Paragraph pChunk = new Paragraph(chunk);
        pChunk.setAlignment(Element.ALIGN_CENTER);
        
        Chapter chapter = new Chapter(pChunk, 1);
        chapter.setNumberDepth(0);
        
        Paragraph pChapter = new Paragraph(daten.spielerName, paragraphFont);
        pChapter.setAlignment(Element.ALIGN_CENTER);
        chapter.add(pChapter);
        
        pChapter = (daten.jahrMax > 0) ?
        		new Paragraph(
        				SternResources.InventurJahr2(false, 
        						Integer.toString(daten.jahr+1), 
        						Integer.toString(daten.jahrMax)),
        				paragraphFont) :
        		new Paragraph(
        				SternResources.InventurJahr1(false, Integer.toString(daten.jahr+1)),
        				paragraphFont);
        pChapter.setAlignment(Element.ALIGN_CENTER);
        chapter.add(pChapter);
        
        pChapter = new Paragraph(
        			SternResources.InventurPunkte(false, 
        				Integer.toString(daten.score)),
        		paragraphFont);
        pChapter.setAlignment(Element.ALIGN_CENTER);
        chapter.add(pChapter);
        
        pChapter = new Paragraph(formatDate(System.currentTimeMillis()), paragraphFont);
        pChapter.setAlignment(Element.ALIGN_CENTER);
        chapter.add(pChapter);
        
        document.add(chapter);
        
        document.add( Chunk.NEWLINE );
        document.add( Chunk.NEWLINE );
        document.add( Chunk.NEWLINE );
        
        for (InventurPdfChapter c: this.daten.chapters)
        {
            final String title = c.chapterName;
            chunk = new Chunk(title, this.linkFont).setLocalGoto(title);
            chunk.setUnderline(0.1f, -2f); //0.1 thick, -2 y-location
            Paragraph p = new Paragraph();
            //p.setFont(this.paragraphFont);
            p.setAlignment(Element.ALIGN_CENTER);
            //p.setIndentationLeft(TOC_INDENT);
            p.add(chunk);
            this.document.add(p);
        }
        
	    // ASCII-Art
        document.add( Chunk.NEWLINE );
        document.add( Chunk.NEWLINE );
        document.add( Chunk.NEWLINE );
        
	    for (String line: ScreenPainter.titelBildTextLines)
	    {
	    		pChapter = new Paragraph(line, this.paragraphFont);
	    		pChapter.setAlignment(Element.ALIGN_CENTER);
	    		document.add(pChapter);
	    }
    }
		
	@Override
    public void onChapter(final PdfWriter writer, final Document document, final float paragraphPosition, final Paragraph title)
    {
        this.pageByTitle.put(title.getContent(), writer.getPageNumber());
    }

	@Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();
        cb.saveState();
        String text = SternResources.InventurSeite(
        		false, Integer.toString(writer.getPageNumber()));
 
        float textBase = document.bottom() - 20;
        float textSize = this.paragraphFont.getBaseFont().getWidthPoint(text, paragraphFont.getCalculatedSize());
         
        cb.beginText();
        cb.setFontAndSize(this.paragraphFont.getBaseFont(), paragraphFont.getCalculatedSize());
        cb.setTextMatrix(((document.getPageSize().getWidth() - textSize) / 2), textBase);
        cb.showText(text);
        cb.endText();
        cb.restoreState();
    }
}
