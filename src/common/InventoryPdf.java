/**	STERN - a strategy game
    Copyright (C) 1989-2021 Michael Schweitzer, spielwitz@icloud.com

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

import commonUi.PanelScreenContent;

class InventoryPdf extends PdfPageEventHelper
{
	private ByteArrayOutputStream outputStream;
	private Document document;
	private PdfWriter writer;
	private InventoryPdfData data;
	
	private com.itextpdf.text.Font chapterFont;
	private com.itextpdf.text.Font paragraphFont;
	private com.itextpdf.text.Font tableFontSmall;
	private com.itextpdf.text.Font linkFont;
	private float charWidth;
		
    private final Map<String, Integer> pageByTitle    = new HashMap<>();
    
	static byte[] create(InventoryPdfData data) throws Exception
	{
		return new InventoryPdf(data).outputStream.toByteArray();
	}
	
	private InventoryPdf(InventoryPdfData data) throws Exception
	{
		this.data = data;
		
		float fontSizeParagraph = this.data.boardOnly ? 6F : 8F;
		
		this.chapterFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 12, Font.BOLD);
		this.paragraphFont = FontFactory.getFont(FontFactory.COURIER, fontSizeParagraph, Font.PLAIN);
		this.tableFontSmall = FontFactory.getFont(FontFactory.COURIER_BOLD, 4, Font.PLAIN);
		this.linkFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.PLAIN, BaseColor.BLUE);
		
		this.outputStream = new ByteArrayOutputStream();
		this.document = new Document();
		this.writer = PdfWriter.getInstance(document, outputStream);
		this.writer.setPageEvent(this);
		this.document.open();
		
		this.charWidth = this.paragraphFont.getBaseFont().getWidthPoint("H", fontSizeParagraph);
		
		if (!this.data.boardOnly)
			this.addCover();
		
		for (int i = 0; i < this.data.chapters.size(); i++)
			this.addChapter(i);
		
		document.close();
	}
	
	private void addChapter(int i) throws Exception
	{
		InventoryPdfChapter c = this.data.chapters.get(i);
		
        final String title = c.chapterName;
        Chunk chunk = new Chunk(title, this.chapterFont).setLocalDestination(title);
        Paragraph p1 = new Paragraph(chunk);
        p1.setAlignment(Element.ALIGN_CENTER);
        final Chapter chapter = new Chapter(p1, i);
        chapter.add(Chunk.NEWLINE);
        chapter.setNumberDepth(0);

        this.document.add(chapter);
        
        if (c.screenContent != null)
        {
	        this.addGraphic(c.screenContent);
	        document.add( Chunk.NEWLINE );
        }
        
        if (c.table != null)
			this.addTable(c.table);
        else
        {
        	chunk = new Chunk(c.messageIfContentNotExists, this.paragraphFont).setLocalDestination(title);
            p1 = new Paragraph(chunk);
            p1.setAlignment(Element.ALIGN_CENTER);
            this.document.add(p1);
        }
   	}
	
	private void addTable(InventoryPdfTable tableData) throws Exception
	{
		float charWidth = tableData.smallFont ?
							this.tableFontSmall.getBaseFont().getWidthPoint("H", 4) :
							this.charWidth;
							
		com.itextpdf.text.Font font = tableData.smallFont ?
							this.tableFontSmall :
							this.paragraphFont;
		
		float[] columnWidths = new float[tableData.colAlignRight.length];
		
		for (int i = 0; i < tableData.cells.size(); i++)
		{
			int columnIndex = i % tableData.colAlignRight.length;
			
			String cellText = tableData.cells.get(i);
			String[] cellTextLines = cellText.split("\n");
			
			for (int j = 0; j < cellTextLines.length; j++)
			{
				float columnWidth = (float)(cellTextLines[j].length() + 1) * charWidth;
				if (columnWidth > columnWidths[columnIndex])
					columnWidths[columnIndex] = columnWidth;
			}
		}
		
		PdfPTable table = new PdfPTable(columnWidths.length);
        table.setTotalWidth(columnWidths);
        table.setLockedWidth(true);
        table.setHeaderRows(1);
        
        for (int i = 0; i < tableData.cells.size(); i++)
		{
			int row = i / tableData.colAlignRight.length;
			int col = i % tableData.colAlignRight.length;
			
			PdfPCell cell = new PdfPCell(new Phrase(tableData.cells.get(i), font));
			cell.setHorizontalAlignment(
					(row == 0 || !tableData.colAlignRight[col]) ?
							Element.ALIGN_LEFT :
							Element.ALIGN_RIGHT);
			
			cell.setNoWrap(true);
			
			if (row == 0 || (tableData.highlightFirstColumn && col == 0))
			{
				cell.setBackgroundColor(GrayColor.LIGHT_GRAY);
			}
			table.addCell(cell);
		}
        
        document.add(table);
	}
	
	private void addGraphic(ScreenContent screenContent) throws Exception
	{
        Rectangle size = document.getPageSize();
		int width = (int)(size.getWidth() - document.leftMargin() - document.rightMargin());		
		
		if (this.data.boardOnly)
			width = (int)(width * 0.45);
		
		double factor = this.data.boardOnly ? 
				(double)(width+ 2 * PanelScreenContent.BORDER_SIZE) / (double)(ScreenPainter.SCREEN_WIDTH - 220) :
				(double)(width+ 2 * PanelScreenContent.BORDER_SIZE) / (double)(ScreenPainter.SCREEN_WIDTH);
		
		// Without console area
		int height = (int)((double)(Constants.BOARD_MAX_Y * ScreenPainter.BOARD_DX+ 2 * PanelScreenContent.BORDER_SIZE) * factor);
        
        PdfContentByte canvas = writer.getDirectContent();
        PdfTemplate template = canvas.createTemplate(width, height);
        Graphics2D g2d = new PdfGraphics2D(template, width, height);
        
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, width, height);
        
        Font fontPlanets = new Font(Font.MONOSPACED, Font.PLAIN, Utils.round((double)PanelScreenContent.FONT_SIZE_PLANETS * factor));
        Font fontMines = new Font(Font.MONOSPACED, Font.PLAIN, Utils.round((double)PanelScreenContent.FONT_SIZE_MINES * factor));
        Font fontSectors = new Font(Font.MONOSPACED, Font.PLAIN, Utils.round((double)PanelScreenContent.FONT_SIZE_SECTORS * factor));
        
        if (!this.data.boardOnly)
        {
	        AffineTransform trans = new AffineTransform();
	        	trans.setToScale(0.92, 0.92);
	        trans.translate((double)PanelScreenContent.BORDER_SIZE / factor, (double)PanelScreenContent.BORDER_SIZE / factor);
	        g2d.setTransform(trans);
        }
                        
        new ScreenPainter(screenContent, true, g2d, fontPlanets, fontMines, fontSectors, factor);
        
        g2d.dispose();
        
        ImgTemplate img = new ImgTemplate(template);
        img.setAlignment(Element.ALIGN_CENTER);
        document.add(img);

	}
	
	private static String formatDate (long time)
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
        
        Paragraph pChapter = new Paragraph(data.playerName, paragraphFont);
        pChapter.setAlignment(Element.ALIGN_CENTER);
        chapter.add(pChapter);
        
        pChapter = (data.yearMax > 0) ?
        		new Paragraph(
        				SternResources.InventurJahr2(false, 
        						Integer.toString(data.year+1), 
        						Integer.toString(data.yearMax)),
        				paragraphFont) :
        		new Paragraph(
        				SternResources.InventurJahr1(false, Integer.toString(data.year+1)),
        				paragraphFont);
        pChapter.setAlignment(Element.ALIGN_CENTER);
        chapter.add(pChapter);
        
        pChapter = new Paragraph(
        			SternResources.InventurPunkte(false, 
        				Integer.toString(data.score)),
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
        
        for (InventoryPdfChapter c: this.data.chapters)
        {
            final String title = c.chapterName;
            chunk = new Chunk(title, this.linkFont).setLocalGoto(title);
            chunk.setUnderline(0.1f, -2f); //0.1 thick, -2 y-location
            Paragraph p = new Paragraph();
            p.setAlignment(Element.ALIGN_CENTER);
            p.add(chunk);
            this.document.add(p);
        }
        
        document.add( Chunk.NEWLINE );
        document.add( Chunk.NEWLINE );
        document.add( Chunk.NEWLINE );
        
	    for (String line: ScreenPainter.titleLinesCount)
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
