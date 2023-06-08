import java.io.*;

public class lab7 {
    // Создание самоподписывающегося сертификата (корневой УЦ, надежный)
    public static String generateKeyPair = "openssl genpkey -algorithm ED448 -out root_keypair.pem";
    public static String signCertificateRequest = "openssl req -new -subj \"/CN=ROOT CA\" -addext \"basicConstraints=critical,CA:TRUE\" -key root_keypair.pem -out root_csr.pem";
    public static String viewRequest = "openssl req -in root_csr.pem -noout -text";
    public static String generateCertificate = "openssl x509 -req -in root_csr.pem -signkey root_keypair.pem -days 3650 -out root_cert.pem";
    public static String viewCertificate = "openssl x509 -in root_cert.pem -noout -text";
    // Несамоподписываемый сертификат (промежуточный УЦ, ненадежный)
    public static String generateIntermediateKeyPair = "openssl genpkey -algorithm ED448 -out intermediate_keypair.pem";
    public static String signIntermediateCertificateRequest = "openssl req -new -subj \"/CN=Intermediate CA\" -addext " +
    "\"basicConstraints=critical,CA:TRUE\" -key " +
    "intermediate_keypair.pem -out intermediate_csr.pem";
    public static String publishIntermediateCertificate = "openssl x509 -req -in intermediate_csr.pem " +
    "-CA root_cert.pem -CAkey root_keypair.pem " +
    "-extfile extensions.cnf -extensions nonLeaf -days 3650 -out intermediate_cert.pem";
    public static String viewIntermediateCertificate = "openssl x509 -in intermediate_cert.pem -noout -text";
    public static String publishLeafCertificate = "openssl genpkey -algorithm ED448 -out leaf_keypair.pem";
    public static String publishLeadCertificate2 = "openssl req -new -subj \"/CN=LEAF\" -addext " +
    "\"basicConstraints=critical,CA:FALSE\" -key leaf_keypair.pem " +
    "-out leaf_csr.pem";
    public static String publishLeafSertificate3 = "openssl x509 -req -in leaf_csr.pem " +
    "-CA intermediate_cert.pem -CAkey intermediate_keypair.pem " +
    "-extfile extensions.cnf -extensions Leaf -days 3650 -out leaf_cert.pem";
    public static String viewLeafCertificate = "openssl x509 -in leaf_cert.pem -noout -text";
    public static String validateCertificate = "openssl verify " +
    "-verbose " +
    "-show_chain " +
    "-trusted root_cert.pem " +
    "-untrusted intermediate_cert.pem " +
    "leaf_cert.pem";
    public static void main(String args[]) throws Exception {

        executeCommand(generateKeyPair, true);
        executeCommand(signCertificateRequest, true);
        executeCommand(viewRequest, true);
        executeCommand(generateCertificate, true);
        //------------------------------------------------

        executeCommand(generateIntermediateKeyPair, true);
        executeCommand(signIntermediateCertificateRequest, true);
        executeCommand(publishIntermediateCertificate, true);
        executeCommand(viewIntermediateCertificate, true);
        executeCommand(publishLeafCertificate, true);
        executeCommand(publishLeadCertificate2, true);
        executeCommand(publishLeafSertificate3, true);
        executeCommand(viewLeafCertificate, true);
        executeCommand(validateCertificate, true);
        
    }

    // Выполнение команды в командной строке Windows
    public static String executeCommand(String command, boolean print) {
        try {
            Process process = new ProcessBuilder("cmd.exe", "/c", command).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line, result = "";
            while ((line = reader.readLine()) != null) {
                result += line;
                if (print)
                    System.out.println(line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Error: " + exitCode);
            }
            return result;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}