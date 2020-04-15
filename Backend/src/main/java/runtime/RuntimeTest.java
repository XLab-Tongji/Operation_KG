package runtime;

import java.io.*;

public class RuntimeTest {
    public static void main(String[] args){
        //解决第三方调用
        String basePath = System.getProperty("user.dir");
        String preCmd="cmd cd "+basePath+"\\src\\main\\java\\runtime\\demo";
        String cmds="cmd /c "+basePath+"\\src\\main\\java\\runtime\\demo\\IPLoM_demo.py --logname=5G.log --pattern=\"<A> <B> <C> <D> <E> <F> <G> <H> <I>: <Content>\"";
        System.out.print(preCmd+"\n");
        System.out.print(cmds);
        Process pcs;
        try {
            pcs = Runtime.getRuntime().exec(cmds);
            // 定义Python脚本的返回值
            String result = null;
            // 获取CMD的返回流
            BufferedInputStream in = new BufferedInputStream(pcs.getInputStream());
            // 字符流转换字节流
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            // 这里也可以输出文本日志

            String lineStr = null;
            while ((lineStr = br.readLine()) != null) {
                result = lineStr;
            }
            // 关闭输入流
            br.close();
            in.close();
            int res=pcs.waitFor();
            System.out.println(result);
            System.out.print(res);
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
