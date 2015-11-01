package io.parallec.sample.app.ssh;

import io.parallec.core.ParallecResponseHandler;
import io.parallec.core.ParallelClient;
import io.parallec.core.ResponseOnSingleTask;
import io.parallec.core.bean.ssh.SshLoginType;

import java.util.Map;

/**
 * Instruction: 
 * put the correct ip address,  username,  [key/password]
 * then try it
 * 
 * tested with AWS Ec2 instance key/passwd
 * @author Yuanteng (Jeff) Pei
 *
 */
public class SshApp {
    
    public static String vmIp = "1.2.3.4";    
    public static String userName = "yourusername";
    public static String password = "yourpassword";
    public static String privKeyRelativePath = "userdata/your-vm-keys.pem";
    public static void main(String[] args) {
        sshVmWithPassword();
        //sshVmWithKey();
    }// end func
    public static void sshVmWithKey() {
        
        ParallelClient pc = new ParallelClient();
        pc.prepareSsh().setConcurrency(150)
                .setTargetHostsFromString(vmIp)
                .setSshLoginType(SshLoginType.KEY)
                .setSshUserName(userName)
                .setSshPrivKeyRelativePath(privKeyRelativePath)
                .setSshCommandLine("df -h; hostname -f; date;  ")
                .execute(new ParallecResponseHandler() {
                    public void onCompleted(ResponseOnSingleTask res,
                            Map<String, Object> responseContext) {
                        System.out.println("Responose:" + res.toString() + " host: "
                                + res.getHost() + " errmsg: "
                                + res.getErrorMessage());
                    }
                });
        pc.releaseExternalResources();
    }
    
    public static void sshVmWithPassword() {
        
        ParallelClient pc = new ParallelClient();
        pc.prepareSsh().setConcurrency(150)
                .setTargetHostsFromString(vmIp)
                .setSshCommandLine("df -h; hostname -f; date;  ")
                .setSshUserName(userName)
                .setSshPassword(password)

                .execute(new ParallecResponseHandler() {

                    @Override
                    public void onCompleted(ResponseOnSingleTask res,
                            Map<String, Object> responseContext) {
                        System.out.println("Responose:" + res.toString() + " host: "
                                + res.getHost() + " errmsg: "
                                + res.getErrorMessage());
                    }
                });
        pc.releaseExternalResources();
    }

    
}
