import java.io.*;
import java.util.ArrayList;


public class lab1{
    public static void main(String[] args) {


        try(FileInputStream fin=new FileInputStream("leasing.txt");
                FileOutputStream fos=new FileOutputStream("leasing_new.txt"))
        {
            byte[] buffer = new byte[256];
           
            ArrayList<Byte> buf = new ArrayList<Byte>();
             
            int count;
            // считываем буфер
            while((count=fin.read(buffer))!=-1){
                for(int i=0; i<count;i++){
             
                
                    buf.add(buffer[i]);
                    if ((char)buffer[i] == ' ') {
                    
                        buf.add((byte)'\b');
                        buf.add((byte)' ');
                    }
            
                }
                byte[] buffer2 = new byte[buf.size()];
                for (Byte byt : buf) {
                    for (int i=0;i<buf.size();i++){
                        buffer2[i] = byt;
                    }
                }
                // for(int j=0; j<buf.size();j++){
             
                //     if (buf.get(j) == 32){
                //         buf.add(j+1,(byte)'\b');
                //         buf.add(j+2,(byte)' ');
                //     }
                    
                // }
                    
                // записываем из буфера в файл
                fos.write(buffer2, 0, buf.size());
                
            }
            // ObjectOutputStream oos = new ObjectOutputStream(fos);
            // oos.writeObject(buf);
            // fos.close();
            
            // System.out.println("File has been written");
            // System.out.println(encrypt(".\\leasing.txt"));
            // System.out.println(encrypt(".\\leasing_new.txt"));
        }
        catch(IOException ex){
              
            System.out.println(ex.getMessage());
        } 
        
        
        
    }

    private static String encrypt(String filePath) {
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "openssl dgst -sha1 " + filePath);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line, result = "";

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                result = line.split("= ")[1];
            }

            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}