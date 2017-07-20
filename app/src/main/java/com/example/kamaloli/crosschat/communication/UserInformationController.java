package com.example.kamaloli.crosschat.communication;

    import org.jivesoftware.smack.AbstractXMPPConnection;

    /**
     * Created by KAMAL OLI on 12/02/2017.
     */

    public class UserInformationController {
        //r=remotelyChatingUser
        String userName,rUserName;
        String usersName,rUsersName;
        String password;
        String email,rEmail;
        String mobileNumber;
        boolean currentStatus,rCurrentStatus;
        AbstractXMPPConnection serverConnection=null;
        public UserInformationController(){}
        public UserInformationController(String userName,String password,String email){
            this.userName=userName;
            this.password=password;
            this.email=email;
        }
        public void setRemotelyChatingUserInfo(String userName,String name,String email){
            rUserName=userName;
            rUsersName=name;
            rEmail=email;
        }
    public String getRemotelyChatingUserName(){
        return rUserName;
    }
    public String getRemotelyChatingUsersname(){
        return rUsersName;
    }
    public String getRemotelyChatingUsersEmail(){
        return rEmail;
    }

    public void setServerConnection(AbstractXMPPConnection serverConnection) {
        this.serverConnection = serverConnection;
    }
   public AbstractXMPPConnection getServerConnection(){
        return serverConnection;
    }
}
