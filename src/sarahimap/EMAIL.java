package sarahimap;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//define the constants
public class EMAIL {
	static String storefolder; 
	static BufferedReader BR; //*for better reading and buffering*//
    static PrintWriter PW ;
    static int LENGTH = 5;
    static int counter = 0;
	protected static final int DEFAULT_SSL_PORT = 993;
    public static String IMAPS="imap.gmail.com";
    protected static final String	TAG_PREFIX="00A";
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

	
	

    
	 
        /**padding with space for length*/
        public static String spacePad(String str){
        String padsp="";
        if(str.length() < LENGTH)
            padsp = new String(new char[LENGTH - str.length()]).replace("\0", " ");

        return padsp + str;
    }     
    
	    /**pad with zeros to match the length*/
	    public static String zeroPad(int no){
	        String noStr = no + "";
	        String padzero="";
	        if(noStr.length() < LENGTH)
	            padzero = new String(new char[LENGTH - noStr.length()]).replace("\0", "0");

	        return padzero+noStr;
	    }
	   
	    /*check if the string is numeric */
	    public static boolean strcheck(String str){
	        return str.matches("\\d+");
	    }
	    
	    /**  this is for get the email address only */
	    public static String Email_add(String str) {
	        if (str.contains("<")) {
	            str = str.substring(str.indexOf("<")+1, str.indexOf(">"));
	        }

	        Pattern emailadd = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
	        Matcher emailmat = emailadd.matcher(str);
	        if (emailmat.find())
	            return emailmat.group(0);
	        return cleanfile(str);
	    }

	    /** check the email if correct or not*/
	    public static boolean Em_vaild(String str){
	        Pattern emailcheck = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
	        Matcher matcher = emailcheck.matcher(str);
	        return matcher.find();
	    }

	    /**clean the string from unwanted characters by using replaceAll */
	    public static String cleanfile(String str){
	        str = str.replaceAll("[^a-zA-Z0-9_ .]", "");
	        
	        return str;
	    }

	    public static String removeBrackets(String str){

	        return str.replaceAll("\\[","").replaceAll("\\]", "");
	    }
	    
	    
	        /**  email process begin*/

	    /**login step ***/  
	    public static String login(String username, String password){
	        String tag, line="", logincmd;
	        tag = TAG_PREFIX + (++counter);
	        logincmd = tag +LOGINIP +username +" " +password;
	        PW.println(logincmd);
	        PW.flush();
	        try {
	            while (!(line = BR.readLine()).contains(tag)) {
	                System.out.println(line);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        System.out.println(LOGINIP  +line +"\n");
	        if(line.toLowerCase().contains(SUCCESS) || line.toLowerCase().contains(COMPLETED) )
	            return SUCCESS;
	        else
	            return ERROR;

	    }

	    /**This method logs out of the server**/
	    public static void logout(){
	        String tag1, line=null, logoutstr;
	        counter=0;
	        tag1 = TAG_PREFIX + (++counter);
	        logoutstr = tag1 +LOGOUTIP;
	        PW.println(logoutstr);
	        PW.flush();
	        try {
	            while (!(line = BR.readLine()).contains(tag1)) {
	                System.out.println(line);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        System.out.println(LOGOUTIP  +" "+line +"\n");
	    }

	    /*** get the folders list */
	    public static ArrayList<String> getList(){
	        String tag, line=null;
	        tag = TAG_PREFIX +(++counter);
	        String listip = "";
	        listip= tag + LIST + "\"\" \"*\"";
	        PW.println(listip);
	        PW.flush();

	        //get a list of mailboxes
	        ArrayList<String> mailboxList = new ArrayList<String>();
	        try {
	            while (!(line = BR.readLine()).contains(tag)) {
	                mailboxList.add(line);
	                System.out.println(line +"\n");
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        System.out.println(LIST +line +"\n");
	        return mailboxList;
	    }

	    /**traversing the corresponding folders and storing emails in corresponding subfolders of the local folder*/
	    public static void traverseList(ArrayList<String> Listmb) {
	    	
	        Pattern listpat = Pattern.compile(LIST_REGEX);
	        String tag, line = null;
	        String mailbox, child_str1 ,child_str2[], HAS_Child ;

	        for (String item: Listmb) {
	            Matcher RM = listpat.matcher(item);
	            int emails = 0;
	            System.out.print("'"+item +"': " +RM.matches() +"\n");
	            if (RM.find()) {
	               child_str1 = RM.group(2).replace("\\", "");// ex: (\Drafts \HasNoChildren)
	               mailbox = RM.group(3); //ex: "INBOX"

	                child_str2 = child_str1.split(" ");
	                 HAS_Child = child_str2[0]; 

	                File mainfolder = new File(storefolder + mailbox);
	                //check if the folder is already exists
	                if (!mainfolder.exists()) {
	                	
	                    if (mainfolder.mkdir()) { 
	                        System.out.println(mailbox + " main folder created ");
	                    }
	                }

	                /**SELECT command**/ 
	                tag = TAG_PREFIX + (++counter);
	                String getcmd;
	               getcmd = tag + SELECT + mailbox;
	                PW.println(getcmd);
	                PW.flush();

	                try {
	                    while (!(line = BR.readLine()).contains(tag)) {
	                        System.out.println(line + "\n");
	                        if (line.contains(EXISTS)) {
	                            String existParts[] = line.split(" ");
	                            for (String part : existParts) {
	                                if (strcheck(part))
	                                    emails = Integer.parseInt(part);
	                            }
	                        }
	                    }

	                    System.out.println(emails + " email(s) found in " + mailbox + "\n");
	                    if(emails > 0) {
	                        System.out.println("Now saving emails...\n");
	                        E_getsave(emails, mailbox);
	                    }
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	                System.out.println(line +"\n");
	                System.out.println("mailbox selected successfully" +"\n");
	            }
	        }

	    }

	    /*** This method fetches and save to a file the emails in a certain folder */
	    public static void E_getsave(int emails_no, String mailBox) {
	        String tag, line, E_filename,from = null, subject = null, meg, fetchcmd , sender_Info , E_index ;

	        if (emails_no > 0)
	            for (int i = 1; i <= emails_no; i++) {
	                tag = TAG_PREFIX + (++counter);
	                fetchcmd = tag + FETCH + i + RFC822_HEADER;
	                PW.println(fetchcmd);
	                PW.flush();
	                System.out.println(i +" of " + emails_no);

	                try {
	                    while (!(line = BR.readLine()).contains(tag)) {
	                        if (line.contains(FROM)) {
	                            from = line.substring(line.indexOf(colon) + 1, line.length()).trim();
	                            if (!from.contains(at))//"at"= @
	                                from += BR.readLine();
	                        }
	                        if (line.contains(SUBJECT))
	                            subject = line.substring(line.indexOf(colon) + 1, line.length()).trim();

	                        System.out.println(line + "\n");

	                    }
	                    System.out.println(line +"\n");
	                    System.out.println( "email header fetched successfully" +"\n");
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }

	             /* as the assignment" Each email will be stored in a file "content.txt", in a folder with the name given by 
                    a unique number on at least 5 digits (obtained by incrementing a counter) 
                    followed by a _ and further by the email address of the sender and after a  
                    further _, by the title of the message which will be of the form 00002_sarah90@gmail.com_title */
	                if (from != null && subject != null) {
	                    sender_Info =  Email_add(from);
	                    subject = cleanfile(subject);
	                    E_index = zeroPad(i);
	                    E_filename = storefolder + mailBox + "/" +  E_index + "_" + sender_Info + "_" + subject;
	                    meg= fetchEmail(i);
	                    saveEmailToFile(E_filename, meg);
	                }
	            }

	    }

	    /**
	     * This method retrieved the message body for a certain email message*/
	    public static String fetchEmail(int index){
	        String tag, line;
	        tag = TAG_PREFIX +(++index);
	        String fetchCommand = tag + FETCH+ index +BODY;
	        String message = "";
	        PW.println(fetchCommand);
	        PW.flush();

	        try {
	            while (!(line = BR.readLine()).contains(tag)) {
	                message += line;
	            }
	            System.out.println(line +"\n");

	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        return message;
	    }

	    
	     /** store the email on file call "content" */
	    public static void saveEmailToFile(String strg, String mesg){
	        PrintWriter content;
	        try {
	          content= new PrintWriter(strg, "UTF-8");
	          content.println(mesg);
	          content.close();
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        }
	    }

	    /*** we need to  authenticate the user */
	    public void Auth_user(String auth){
	        String tag, line="", Auth_cmd;
	        tag = TAG_PREFIX + (++counter);
	        Auth_cmd = tag + AUTHENTICATE + auth;

	        PW.println(Auth_cmd);
	        PW.flush();
	        try {
	            while (!(line = BR.readLine()).contains(tag)) {
	                System.out.println(line);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        System.out.println(AUTHENTICATE  +line +"\n");//success

	    }


	    
	    /**the main class to this program*/
	    public static void main(String args[]) {
	        try {
	            String opensslCommand = "openssl s_client -crlf -connect ";
	            String server = IMAPS;
	            String Save_Folder = "";
	            

	            if (args.length > 0) {
	                server = args[0];
	            }

	            if (args.length > 1) {
	            	Save_Folder = args[1];
	            }

	            opensslCommand += server + colon + DEFAULT_SSL_PORT;
	            String email = "", pwd, loginMsg;
	            Scanner scanner = new Scanner(System.in);

	            //call Em_vaild function
	            while (!Em_vaild(email)) {
	                System.out.print("Email: ");
	                email = scanner.nextLine();
	            }

	            System.out.print("Password: ");
	            pwd = scanner.nextLine();

	            Process openssl = Runtime.getRuntime().exec(opensslCommand);
	            InputStream is = openssl.getInputStream();
	            BufferedReader BR = new BufferedReader(new InputStreamReader(is));
	            PrintWriter PW  = new PrintWriter(new OutputStreamWriter(openssl.getOutputStream()));

	           
	            loginMsg = login(email, pwd);

	            //login to fetch mails
	            if (loginMsg.equals(SUCCESS)) {
	                ArrayList<String> list = getList();
	               traverseList(list);
	               logout();
	            }

	            BR.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	}
	    
	    
	    
	
	



