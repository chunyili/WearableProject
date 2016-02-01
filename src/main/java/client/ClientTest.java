package client;

import resource.Client_BS_Obj;

/**
 * Created by jilongsun on 7/15/15.
 */
public class ClientTest {
    public static void main(String args[])throws Exception{

        Client_BS_Obj clientObj = BootstrapRequest.getBootInfo();

        if(clientObj == null){
            System.out.println("Bootstrap has been denied, cannot register");
        }else {
            String returnString = RegisterRequest.registerInfo(clientObj);
            System.out.print(returnString);
        }




    }
}
