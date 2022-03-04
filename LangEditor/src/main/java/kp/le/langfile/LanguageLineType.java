package kp.le.langfile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import kp.le.editor.EntryType;
import kp.le.util.Utils;

/**
 *
 * @author Marc
 */
public final class LanguageLineType
{
    private LanguageLineType() {}
    
    private static final HashMap<Integer, EntryData> entries = new HashMap<>();
    private static final EnumMap<EntryType, Set<Integer>> entryIndicesByType = new EnumMap<>(EntryType.class);
    private static boolean initiated = false;
    
    public static final EntryData getData(int index)
    {
        if(!initiated)
        {
            init();
            initiated = true;
        }
        
        var data = entries.getOrDefault(index, null);
        if(data == null)
            data = new EntryData(index, EntryType.NORMAL);
        else
            data = data.copy();
        return data;
    }
    
    public static final int[] getTypeIndices(EntryType type)
    {
        var set = entryIndicesByType.getOrDefault(type, null);
        if(set == null)
            return new int[] {};
        
        return set.stream().mapToInt(Integer::intValue).toArray();
    }
    
    private static void init()
    {
        try(var is = Utils.getClasspathResourceAsStream("message_indices.dat"))
        {
            loadTypeFile(is, EntryType.MESSAGE);
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
        
        try(var is = Utils.getClasspathResourceAsStream("level_indices.dat"))
        {
            loadTypeFile(is, EntryType.LEVEL);
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void loadTypeFile(InputStream is, EntryType type)
    {
        var set = new HashSet<Integer>();
        try(var reader = new BufferedReader(new InputStreamReader(is)))
        {
            String line;
            while((line = reader.readLine()) != null)
            {
                var parts = line.split(":");

                if(parts.length != 2)
                    throw new IllegalStateException("Invalid file format.");

                int id = Integer.parseInt(parts[0]);
                int index = Integer.parseInt(parts[1]);

                entries.put(index, new EntryData(id, type));
                set.add(index);
            }
            
            entryIndicesByType.put(type, Set.copyOf(set));
            set.clear();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }
    
    public static record EntryData(int id, EntryType type)
    {
        public final EntryData copy() { return new EntryData(id, type); }
    }
}
