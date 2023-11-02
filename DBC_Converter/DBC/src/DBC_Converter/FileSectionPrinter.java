package DBC_Converter;

public class FileSectionPrinter {
	public StringBuilder head;
	public StringBuilder body;
	public StringBuilder end;

	public FileSectionPrinter()
	{
	    head = new StringBuilder(10000);
	    body = new StringBuilder(10000);
	    end = new StringBuilder(10000);
	}
}
