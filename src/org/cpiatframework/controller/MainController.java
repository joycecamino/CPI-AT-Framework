package org.cpiatframework.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.cpiatframework.config.Constants;
import org.cpiatframework.util.BrowserTest;
import org.cpiatframework.util.Excel;
import org.cpiatframework.util.Upload;

@WebServlet("/MainController")
public class MainController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String filePath;
	private String page;
	private String ipAddress = "";
	private List<String> crossBrowserResult = new ArrayList<String>();
	private String[][] browsers = {
				{"Firefox", Constants.FIREFOX_DRIVER, Constants.BROWSER_PROPERTY_FIREFOX},
				{"Chrome", Constants.CHROME_DRIVER, Constants.BROWSER_PROPERTY_CHROME}
			};
	private String folderDate = "";
	
	public void init( ){
		filePath = Constants.PATH_UPLOAD;
	}
         
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		page = "view/error.jsp";
		String action = request.getParameter("action");
		RequestDispatcher dispatcher = null;
		if(action.equals("execute")){
			boolean isMultipart;
			isMultipart = ServletFileUpload.isMultipartContent(request);
			if( !isMultipart ){
				page = "view/error.jsp";
			} else {
				page = "view/result.jsp";
				String project = request.getParameter("projectName");
				String qa = request.getParameter("qaName");
				ipAddress = request.getParameter("ipaddress");
				SimpleDateFormat ft = new SimpleDateFormat ("MM-dd-yyyy hh.mm.ss a");
				folderDate = ft.format(new Date());
				Upload fileUpload = new Upload(request, filePath);
				BrowserTest browserTest = new BrowserTest(browsers, fileUpload.uploadFile(), ipAddress, project, folderDate);
				try {
					crossBrowserResult = browserTest.testcase(filePath);
				} catch (EncryptedDocumentException e) {
					e.printStackTrace();
				} catch (InvalidFormatException e) {
					e.printStackTrace();
				}
//				try {
//					savePDF();
//				} catch (DocumentException e) {
//					e.printStackTrace();
//				}
			}
		}
		dispatcher = request.getRequestDispatcher(page);
		dispatcher.forward(request, response);
	}
	
	protected void savePDF(String project, String qa) throws IOException, DocumentException {
		Date date = new Date();
		//SimpleDateFormat ft = new SimpleDateFormat ("MM-dd-yyyy hh.mm.ss a");
		
		/*try {
			pdfUtility.WriteTestResultToPdfFile(project + "-" + ft.format(date) + ".pdf", crossBrowserResult);
		} catch (COSVisitorException e) {
			e.printStackTrace();
		}*/
		 
		
		String pdfPath = "C:\\Users\\cpi\\Desktop\\Selenium\\Selenium Udemy Workspace\\CPI-AT-MyCopy\\" + project + "-" + folderDate + "\\" + project + "-" + folderDate + ".pdf";
		
		Document document = new Document(PageSize.A4, 36, 36, 120, 54);
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfPath)); 
		//System.out.println(writer.isPageEmpty());
		document.open();
		//header
		Rectangle headerBox = new Rectangle(36, 54, 559, 788);
		HeaderFooter event = new HeaderFooter();
		writer.setBoxSize("headerBox", headerBox);
		writer.setPageEvent(event);
		
		
		Paragraph preface = new Paragraph();
		preface.add(new Paragraph("Project: " + project.toUpperCase(), FontFactory.getFont(FontFactory.TIMES,18, Font.BOLDITALIC, BaseColor.BLACK)));
		
		SimpleDateFormat ft2 = new SimpleDateFormat ("E MM/dd/yyyy 'at' hh:mm:ss a");

		preface.add(new Paragraph( "Report generated by: " + qa + ", " + ft2.format(date), FontFactory.getFont(FontFactory.TIMES,14, Font.BOLD, BaseColor.BLACK)));
		addEmptyLine(preface, 1);
		
		//Paragraph title1 = new Paragraph("Project: " + project,FontFactory.getFont(FontFactory.TIMES,18, Font.BOLDITALIC, new CMYKColor(0, 255, 255,17)));
		//Chapter chapter1 = new Chapter(title1);
		//chapter1.setNumberDepth(0);
		
		//Paragraph title11 = new Paragraph("QA: " + qa,FontFactory.getFont(FontFactory.TIMES, 16, Font.BOLD,new CMYKColor(0, 255, 255,17)));
		//Section section1 = chapter1.addSection(title11);
		Paragraph someSectionText = new Paragraph("Results:");
		preface.add(someSectionText);
//		PdfPTable t = null;
		
	   
		//document.add(new Paragraph(message));
		document.add(preface);
	    try{
			for(String finalResult : crossBrowserResult){
				
				//t = new PdfPTable(3);
				
				PdfPTable t = new PdfPTable(3);
				t.setSpacingBefore(15);
				t.setSpacingAfter(25);
				
			    PdfPCell c1 = new PdfPCell(new Phrase("STEPS"));
			    c1.setBackgroundColor(new CMYKColor(0, 1, 0, 24));
			    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			    t.addCell(c1);
			    PdfPCell c2 = new PdfPCell(new Phrase("RESULT"));
			    c2.setBackgroundColor(new CMYKColor(0, 1, 0, 24));
			    c2.setHorizontalAlignment(Element.ALIGN_CENTER);
			    t.addCell(c2);
			    PdfPCell c3 = new PdfPCell(new Phrase("REMARKS"));
			    c3.setBackgroundColor(new CMYKColor(0, 1, 0, 24));
			    c3.setHorizontalAlignment(Element.ALIGN_CENTER);
			    t.addCell(c3);
				String[] lines = finalResult.split(",");
				for(String line: lines){
					String[] cells = line.split("-");
					
					if (cells.length == 3) {
						for(int i=0; i < 3; i++){
							if (cells[i].equals("PASSED")){
								//Paragraph cPassed = new Paragraph(cells[i],FontFactory.getFont(FontFactory.TIMES,14, Font.ITALIC, BaseColor.GREEN));
								PdfPCell cPassed = new PdfPCell(new Paragraph(cells[i],FontFactory.getFont(FontFactory.TIMES,12, Font.NORMAL, BaseColor.BLACK)));
								cPassed.setBackgroundColor(new CMYKColor(19, 1, 18, 17));
								t.addCell(cPassed);
							}else if (cells[i].contains("FAILED")){
								PdfPCell cFailed = new PdfPCell(new Paragraph(cells[i],FontFactory.getFont(FontFactory.TIMES,12, Font.NORMAL, BaseColor.BLACK)));
								cFailed.setBackgroundColor(new CMYKColor(0, 41, 33, 0));
								t.addCell(cFailed);
							} else {
								Paragraph c = new Paragraph(cells[i] + " " + project,FontFactory.getFont(FontFactory.TIMES,12, Font.NORMAL, BaseColor.BLACK));
								t.addCell(c);
							}   
						}
					} else {
						for(int i=0; i < cells.length; i++){
							if (cells[i].equals("PASSED")){
								//Paragraph cPassed = new Paragraph(cells[i],FontFactory.getFont(FontFactory.TIMES,12, Font.ITALIC, BaseColor.GREEN));
								//t.addCell(cPassed);
								PdfPCell cPassed = new PdfPCell(new Paragraph(cells[i],FontFactory.getFont(FontFactory.TIMES,12, Font.NORMAL, BaseColor.BLACK)));
								cPassed.setBackgroundColor(new CMYKColor(19, 1, 18, 17));
								t.addCell(cPassed);
							}else if (cells[i].contains("FAILED")){
								PdfPCell cFailed = new PdfPCell(new Paragraph(cells[i],FontFactory.getFont(FontFactory.TIMES,12, Font.NORMAL, BaseColor.BLACK)));
								cFailed.setBackgroundColor(new CMYKColor(0, 41, 33, 0));
								t.addCell(cFailed);
							} else {
								Paragraph c = new Paragraph(cells[i],FontFactory.getFont(FontFactory.TIMES,12, Font.NORMAL, BaseColor.BLACK));
								t.addCell(c);
							}     
						}
						for(int i=0; i < 3 - cells.length; i++){
							t.addCell(" ");   
						}
					}
						
				}
				//section1.add(t);
				//preface.add(t);
				document.add(t);
				t.flushContent();
			}
			
			//preface.clear();
			
	    } catch (Exception e){
	    	e.printStackTrace();
	    } finally {
			document.close();
			writer.close();
	    }
		
		//document.add(chapter1);
		
		

	}
	
	 private static void addEmptyLine(Paragraph paragraph, int number) {
         for (int i = 0; i < number; i++) {
                 paragraph.add(new Paragraph(" "));
         }
	 }
	
	static class HeaderFooter extends PdfPageEventHelper {

		  public void onEndPage(PdfWriter writer, Document document) {
		    Rectangle rect = writer.getBoxSize("headerBox");
		    // add header text
		   // ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_RIGHT, new Phrase("Hello"),rect.getLeft(), rect.getTop(), 0);

		    // add header image
		    try {
		      Image img = Image.getInstance("C:\\Users\\cpi\\Desktop\\Selenium\\Selenium Udemy Workspace\\CPI-AT-MyCopy\\WebContent\\imgs\\cpi_logo.png");
		      img.scaleToFit(100,100);
		      img.setAbsolutePosition(35,742); 
		      writer.getDirectContent().addImage(img);
		      //document.add(img);
		    } catch (Exception x) {
		      x.printStackTrace();
		    }

		  }

		}

	 /*public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
	        response.setContentType("application/zip");
	        response.setHeader("Content-Disposition", "attachment; filename=data.zip");

	        // You might also wanna disable caching the response
	        // here by setting other headers...

	        try ( ZipOutputStream zos = new ZipOutputStream(response.getOutputStream()) ) {
	            // Add zip entries you want to include in the zip file
	        }
	 }*/
	 
	 protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*String fileName = request.getParameter("fileName");
		String courseId = request.getParameter("courseId");
		String type = request.getParameter("userType");
		String download = request.getParameter("download");
		String courseTitle = request.getParameter("courseTitle");*/
		String download = request.getParameter("download");
		String name = project + "-" + folderDate + ".zip";
		String path = "C:\\Users\\cpi\\Desktop\\Selenium\\Selenium Udemy Workspace\\CPI-AT-MyCopy\\" + project + "-" + folderDate;
		
		//String outputZipPath = "C:\\Users\\cpi\\Desktop";
		File file = new File(path+File.separator+name);
		File allFiles = new File(path);
		//File downloadAll = new File(path+File.separator+name);
		
		
		if(download.equalsIgnoreCase("all")){
			/*String zipFile = outputZipPath+File.separator+name;
			FileOutputStream fout = new FileOutputStream(zipFile);
			ZipOutputStream zout = new ZipOutputStream(fout);
			if (allFiles.exists()){
				try {
					 File[] listOfFiles = allFiles.listFiles();
					 for (int i = 0; i < listOfFiles.length; i++) {
						 byte[] buffer = new byte[1024];
						 FileInputStream fin = new FileInputStream(listOfFiles[i]);
						 zout.putNextEntry(new ZipEntry(listOfFiles[i].getName()));
						 int length;
               
                      while((length = fin.read(buffer)) > 0){
                         zout.write(buffer, 0, length);
                      }
                      zout.closeEntry();
                      fin.close();
					 }
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					zout.close();
				}*/
			try{
				String output_zip_file = path+File.separator+name;
				String source_folder = path;
				AppZip appZip = new AppZip(output_zip_file, source_folder);
				appZip.generateFileList(new File(source_folder));
				appZip.zipIt(output_zip_file);
				
				File downloadAll = new File(output_zip_file);
				ServletContext ctx = getServletContext();
				InputStream fis = new FileInputStream(downloadAll);
				String mimeType = ctx.getMimeType(downloadAll.getAbsolutePath());
				response.setContentType(mimeType != null? mimeType:"application/octet-stream");
				response.setContentLength((int) downloadAll.length());
				response.setHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");

				ServletOutputStream os = response.getOutputStream();
				byte[] bufferData = new byte[1024];
				int read=0;
				while((read = fis.read(bufferData))!= -1){
					os.write(bufferData, 0, read);
				}
				os.flush();
				os.close();
				fis.close();
			}catch (Exception ex){
				ex.printStackTrace();
			} finally {
				deleteDir(allFiles);
				System.out.println("Deleted");
			}
		}
		
	 }

	 public void deleteDir(File file) {
		 Boolean isDeleted = false;
		    File[] contents = file.listFiles();
		    if (contents != null) {
		        for (File f : contents) {
		        	isDeleted = f.delete();
		        	System.out.println(f.toString() + isDeleted);
		            deleteDir(f);
		        }
		    }
		    file.delete();
	}

}
