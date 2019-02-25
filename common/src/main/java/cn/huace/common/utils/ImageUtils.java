package cn.huace.common.utils;

public class ImageUtils
{
    /**
     * <获取url存放路径>
     * 
     * @param paramString
     * @return
     */
    public static String getFileDir(String paramString)
    {
        int i = 0;
        int j = 1;
        while ((i < 3) && (j > 0))
        {
            j = paramString.indexOf("/", j + 1);
            if (j <= 0)
            {
                continue;
            }
            i++;
        }
        if ((j > 0) && (i == 3))
        {
            int k = paramString.lastIndexOf("/");
            if (k > 0)
            {
                return paramString.substring(j + 1, k + 1);
            }
            return "";
        }
        return "";
    }
    
    public static String getFileName(String paramString)
    {
        int i = paramString.lastIndexOf("/");
        if (i > 0)
        {
            return paramString.substring(i + 1);
        }
        return paramString;
    }
    
    public static String getFileLocation(String paramString)
    {
        int i = paramString.lastIndexOf("/");
        if (i > 0)
        {
            return paramString.substring(0, i);
        }
        return paramString;
    }
    
    public static String getPrefix(String paramString)
    {
        int i = paramString.lastIndexOf(".");
        if (i > 0)
        {
            return paramString.substring(i + 1);
        }
        return paramString;
    }
    
}
