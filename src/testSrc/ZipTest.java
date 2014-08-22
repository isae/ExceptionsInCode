package testSrc;

import com.jetbrains.isaev.dao.ZipUtils;

/**
 * Created by Ilya.Isaev on 01.08.2014.
 */
public class ZipTest {
    public static void main(String[] args) {
        String tmp = "OMFG LOL AZZsvglkjmdg;lkdfjg;pdslkjg;glkjdes;'lkgjdsglkop'dfdfgdfg'jdfsgo'dfsgop'jdfsgop'jdfsgop'jdfsgop'jfdgds'pkgjxvc;bkmsdf'gpo";
        System.out.println(tmp);
        byte[] tmp2 = ZipUtils.compress(tmp);
        String tmp3 = ZipUtils.decompress(tmp2);
        assert tmp.equals(tmp3);
    }

}
