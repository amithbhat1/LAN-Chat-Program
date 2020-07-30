import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.io.*;
import java.util.*;

class clientThread extends Thread
{



     Socket clientSocket=null;
    clientThread[] user;
    int maxClientCount;
    String id;
    String pass;
    int key = 2;   
    boolean online;
    int avail;
    int inbox;
   public  String messages;
   public  String whoms;
     //private String user;

     //creating  the constructor

    public clientThread(Socket clientSocket,clientThread[] user)
    {
    	this.clientSocket=clientSocket;
    	this.user=user;
    	this.maxClientCount=user.length;
        this.avail=0;
        this.online=false;
        this.inbox=0;
    }/*
    public int login(String id,String pass)//for login
    {
        int x = check(id,pass);
        if(x==1)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }
    */
    public int check(String username)
    {

        int i=0,k=0;
                for(i=0;i<maxClientCount;i++)
                {
                     if(user[i]!=null&&user[i]!=this)
                     {
                        if(user[i].id.equals(username))
                        {
                            
                            k++;
                        }
                     }

                }
        
        return k;
    }
    public void send_message(String mess,String username)
    {
        
            int i=0;
            for(i=0;i<maxClientCount;i++)
                {
                     if(user[i]!=null&&user[i]!=this)
                     {
                        if(user[i].id.equals(username))
                        {
                           
                           user[i].messages=mess; 
                           user[i].whoms=id;
                           System.out.println("hi i am here\n");
                           
                        }
                     }

                }


    }

   public void run()
   {

     try
     {
     	//create input and output for this client
        
      
      PrintWriter out=new PrintWriter(clientSocket.getOutputStream(),true);
      BufferedReader in=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
   
      
      int  n;
      //out.println("now i am normal");
      while(true)
   {  
      n=Integer.parseInt(in.readLine());
      if(n==2)
      {
        //out.println("hi");
        //sign up function
         String username;
         String password;
         username=in.readLine();
         password=in.readLine();
         //out.println("hi");
         int k=0;
         int i;
         for(i=0;i<maxClientCount;i++)
         {
            
            if(user[i]!=null&&user[i]!=this)
            {
                //out.println("I am in");
                if(user[i].id.equals(username))
                    {
                        out.println("Sorry this username is already existed so please try other one\n");
                        k++;
                    }
            }
         }
         if(k==0)
         {
            id=username;
            pass=password;
            out.println("Signup Successfull\n");
            avail++;
         }
      }
      else if(n==1)
      {
        //login part
        //out.println("I am in");
          String username;
          String password;
          username=in.readLine();
          password=in.readLine();
          if(id.equals(username)&&pass.equals(password))
          {
            out.println("login Successfull");
            System.out.println("login Successfull");
            out.println("1");
            int k;
            k=Integer.parseInt(in.readLine());
            if(k==1)
            {
                String reciever;
                reciever=in.readLine();
                int rec;
                rec=check(reciever);
                if(rec==0)
                {
                    out.println("No user exists of that name");
                }
                if(rec!=0)
                {

                     ///out.println("hey I am here");
                     String mess=in.readLine();
                     System.out.println(mess);
                     send_message(mess,reciever);
                     out.println("message sent Successfully");

                }
            }
            else if(k==2)
            {

            }
            else if(k==3)
            {
                     
                     out.println(this.whoms);
                     out.println(this.messages);

            }

          }
          else
          {
            out.println("login Unsuccessfull");
          }

      }
    }

    }
    catch(IOException e)
    {
    	System.out.println("Exception"+e);
    }
   }

}

//A chat Server that delivers public and private messages
class serverside
{
     //creating main Server Socket
	private static ServerSocket serverSocket=null;
	//creating client Socket
	private static Socket clientSocket=null;

	//this server can accept maximum of 10clients
	private static final int maxClientCount=10;
    private static final clientThread[] user=new clientThread[maxClientCount];

    //main function
    public static void main(String args[])
    {
    	//The default port no
    	int portno;
        Scanner sc=new Scanner(System.in);
    	/*if(args.length<1)
    	{
    		System.out.println("Server-port no:"+portno);
    	}*/

    	System.out.println("enter the port no");
        portno=sc.nextInt();


    	//opening a server socket

    	try
    	{
    		serverSocket=new ServerSocket(portno);
    	}
    	catch(IOException e)
    	{
    		System.out.println(e);
    	}

    	//create s client socket for each connection and pass it to a new client thread
while(true)
{
	try
	{
		
       //accepting the clients when it calls  
        clientSocket=serverSocket.accept();
        System.out.println(" client is connected");
		int i=0;
		for(i=0;i<maxClientCount;i++)
		{
			if(user[i]==null)
			{    
                System.out.println("hi i called the thread\n");
				(user[i]=new clientThread(clientSocket,user)).start();
				break;
			}
		}
         
      if(i==maxClientCount)
      {
        PrintWriter out=new PrintWriter(clientSocket.getOutputStream(),true);
        out.println("System is too busy\n");
      	clientSocket.close();
      }
     }
     catch(IOException e)
     {
     	System.out.println(e);
     }
}
}
}

