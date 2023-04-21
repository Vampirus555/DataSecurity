import java.io.*;

public class lab1{
    public static void main(String[] args) {


        try(FileInputStream fin=new FileInputStream("leasing.txt");
                FileOutputStream fos=new FileOutputStream("leasing_new.txt"))
        {
            byte[] buffer = new byte[256];
             
            int count;
            // считываем буфер
            while((count=fin.read(buffer))!=-1){
                if (count == ' ')
                    count = '!';
                // записываем из буфера в файл
                fos.write(buffer, 0, count);
            }
            System.out.println("File has been written");
        }
        catch(IOException ex){
              
            System.out.println(ex.getMessage());
        } 

        // ProcessBuilder builder = new ProcessBuilder();
        // builder.command("cmd.exe", "/c", "openssl dgst -sha1 .\\leasing.txt");
        // try {

        //     Process process = builder.start();
    
        //     StringBuilder output = new StringBuilder();
    
        //     BufferedReader reader = new BufferedReader(
        //             new InputStreamReader(process.getInputStream()));
    
        //     String line;
        //     while ((line = reader.readLine()) != null) {
        //         output.append(line + "\n");
        //     }
    
        //     int exitVal = process.waitFor();
        //     if (exitVal == 0) {
        //         System.out.println("Success!");
        //         System.out.println(output);
        //         System.exit(0);
        //     } else {
        //         //abnormal...
        //     }
    
        // } catch (IOException e) {
        //     e.printStackTrace();
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
    }
}