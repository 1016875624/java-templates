import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import org.junit.Test;

import java.io.*;
import java.util.List;

public class WebDavTest {
    String serverAddress = "xxx";
    String userName = "xxx";
    String password = "xxx";
    String fileDir = "myfile/";
    @Test
    public void test() throws IOException {
    }

    public void getFile(String filePath,String savePath) throws IOException {
        File file = new File(savePath);
        Sardine sardine = SardineFactory.begin(userName, password);
        try (InputStream inputStream = sardine.get(serverAddress + fileDir + filePath)){
            try (BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(file))){
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                bo.write(bytes);
                bo.flush();
            }
        }
    }
    /**
     * 读取文件夹的内容
     * @throws IOException
     */
    public void readAllfile(String dirPath) throws IOException {
        Sardine sardine = SardineFactory.begin(userName, password);
        List<DavResource> resources = sardine.list(serverAddress + dirPath);
        for (DavResource res : resources) {
            System.out.println(res);
        }
    }

    /**
     * 上传文件
     * @param file
     * @throws IOException
     */
    public void putFile(File file) throws IOException {
        Sardine sardine = SardineFactory.begin(userName, password);
        sardine.createDirectory(serverAddress + fileDir);
        try (BufferedInputStream bi = new BufferedInputStream(new FileInputStream(file))){
            byte[] bytes = new byte[bi.available()];
            bi.read(bytes);
            sardine.put(serverAddress + fileDir + file.getName(), bytes);
        }
    }
}
