package common;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class FileList
{
    NameFilter filter = null;
    ArrayList<String> paths = new ArrayList<String>();
    
    public FileList(String path, String ext)
    {
        filter = new NameFilter(ext);
        listAll(path);
    }
    
    public int getFileNum()
    {
        return paths.size();
    }
    
    public String getFile(int i)
    {
        if(i < paths.size())
            return paths.get(i);
        return "";
    }
    
    
    /*
     * recursive list all files in specified directory
     * @param path: dir/file name
     */
    private void listAll(String path)
    {
        File f = new File(path);
        if (!f.exists())
            return;

        if (f.isFile())
            paths.add(path);
        else if (f.isDirectory())
        {
            String objects[] = f.list(filter);
            for (int i=0; i<objects.length; i++)
            {
                listAll(path + File.separator + objects[i]);
            }
        } 
    }

    class NameFilter implements FilenameFilter
    {
        String extension = "";
        public NameFilter(String ext)
        {
            extension = "." + ext;
        }
        
        @Override
        public boolean accept(File dir, String name)
        {
            File f = new File(dir.toString() + "\\" + name);
            if(f.isDirectory())
                return true;
            
            int len = extension.length();
            int len1 = name.length();
            if(len1 < len)
                return false;
            String subStr = name.substring(len1-len, len1);
            return subStr.equalsIgnoreCase(extension);
        }
    }
}
