
package sarahimap;


public class Constant {

	protected static final int DEFAULT_SSL_PORT = 993, LENGTH = 5;;
    public static String IMAPS="imap.gmail.com";
    protected static final String	TAG_PREFIX="A";
	public static String LOGINIP="LOGIN";
	public static String LOGOUTIP="LOGINOUT";
	public static int passward ;
	public static String SUCCESS ="success";
	public static final java.lang.String AUTHENTICATE= "AUTHENTICATE";
    static final String SKEY="SKEY";
    public static String ERROR ="error";
    public static String BODY =" body";
    public static String FETCH =" FETCH";
    public static String SELECT=" SELECT ";
    public static String NOSELECT="Noselect"; 
    public static String COMPLETED = "completed";
    public static String EXISTS ="exists";
    public static final String LIST="LIST";
    public static String haschildren ="HasChildren";
    public static String SUBJECT ="Subject:";
    public static String FROM ="From:";
    public static String colon =":";
    public static String at ="@";
    public static final String RFC822_HEADER="RFC822.HEADER";
    public static final String RFC822_SIZE="RFC822.SIZE";
    public static final String	RFC822_TEXT	="RFC822.TEXT";
    public static String FROM_SUBJECT_TAG =" (body[header.fields (\"from\" \"subject\")])";
    public static String LIST_REGEX ="^(\\* LIST \\(([ ?\\x5Ca-zA-Z]+)\\) \"/\" \"([\\[?\\w+\\]?/?]+)\")$";
    public static final String EMAIL_REGEX ="^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";


}
