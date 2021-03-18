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

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;

public class PdfLauncher
{
	public static boolean showPdf(byte[] pdfBytes)
	{
		boolean success = false;
		try {
			File tempFile = File.createTempFile("Stern", ".pdf");
			tempFile.deleteOnExit();
			
			FileOutputStream fos = new FileOutputStream(tempFile.getAbsolutePath());
			fos.write(pdfBytes);
			fos.close();
			
			if (Desktop.isDesktopSupported()) {
	            File myFile = new File(tempFile.getAbsolutePath());
	            Desktop.getDesktop().open(myFile);
	            success = true;
	        }

		} catch (Exception e) {
		}

		return success;
	}
}
