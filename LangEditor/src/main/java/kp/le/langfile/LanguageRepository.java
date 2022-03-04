package kp.le.langfile;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import kp.le.editor.EntryType;
import kp.le.util.Utils;

/**
 *
 * @author Marc
 */
public class LanguageRepository
{
    private final ArrayList<String> lines = new ArrayList<>();
    
    public final int getLinesCount() { return lines.size(); }
    public final boolean isEmpty() { return lines.isEmpty(); }
    
    public final String getLine(int index) { return lines.get(index); }
    
    public final void setLine(int index, String line)
    {
        var data = line.getBytes(Utils.POP_LANG_CHARSET);
        lines.set(index, new String(data, Utils.POP_LANG_CHARSET));
    }
    
    public final LineReference getLineReference(int index)
    {
        return new LineReference(Objects.checkIndex(index, lines.size()));
    }
    
    public final List<LineReference> getLinesReferences(int... indices)
    {
        if(indices == null || indices.length < 1)
            return List.of();
        
        if(indices.length == 1)
            return List.of(getLineReference(indices[0]));
        
        return IntStream.of(indices)
                .distinct()
                .sorted()
                .mapToObj(this::getLineReference)
                .toList();
    }
    
    public final List<LineReference> getLinesReferences(Collection<Integer> indices)
    {
        var array = indices.stream()
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .toArray();
        return getLinesReferences(array);
    }
    
    public final List<LineReference> getAllLinesReferences()
    {
        return IntStream.range(0, lines.size())
                .mapToObj(LineReference::new)
                .toList();
    }
    
    public final void read(Path filepath) throws IOException
    {
        try(var input = Files.newInputStream(filepath))
        {
            read(input);
        }
    }
    
    public final void readTemplate()
    {
        try(var input = Utils.getClasspathResourceAsStream("template_text.dat"))
        {
            read(input);
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }
    
    private void read(InputStream input) throws IOException
    {
        var list = new LinkedList<String>();
        byte[] fileBuffer = new byte[8192];
        byte[] textBuffer = new byte[fileBuffer.length / 2];
        int len;

        int tIdx = 0;
        while((len = input.read(fileBuffer)) > 0)
        {
            for(int fIdx = 0; fIdx < len; fIdx += 2)
            {
                if(fileBuffer[fIdx] == 0)
                {
                    var tLine = new String(textBuffer, 0, tIdx, Utils.POP_LANG_CHARSET);
                    list.add(tLine);
                    tIdx = 0;
                }
                else
                {
                    textBuffer[tIdx] = fileBuffer[fIdx];
                    tIdx++;
                }
            }
        }
        
        lines.clear();
        lines.ensureCapacity(list.size());
        lines.addAll(list);
    }
    
    public final void write(Path filepath) throws IOException
    {
        int maxLen = Utils.max(lines, String::length);
        byte[] buffer = new byte[(maxLen * 2) + 2];
        
        try(var out = new BufferedOutputStream(Files.newOutputStream(filepath)))
        {
            for(var line : lines)
            {
                byte[] data = line.getBytes(Utils.POP_LANG_CHARSET);
                int bufflen = (data.length * 2) + 2;
                
                for(int i = 0; i < data.length; ++i)
                {
                    int idx = i * 2;
                    buffer[idx] = data[i];
                    buffer[idx + 1] = 0;
                }
                buffer[bufflen - 1] = 0;
                buffer[bufflen - 2] = 0;
                
                out.write(buffer, 0, bufflen);
            }
        }
    }
    
    public final void dump(OutputStream out)
    {
        try(var ps = new PrintStream(out))
        {
            lines.forEach(ps::println);
        }
    }
    
    public final class LineReference implements Comparable<LineReference>
    {
        private final int index;
        
        private LineReference(int index)
        {
            this.index = index;
        }
        
        public final int getIndex() { return index; }
        
        public final String get() { return getLine(index); }
        public final void set(String line) { setLine(index, line); }
        
        public final LanguageLineType.EntryData getData() { return LanguageLineType.getData(index); }
        public final int getId() { return getData().id(); }
        public final EntryType getType() { return getData().type(); }
        
        @Override
        public final String toString() { return getLine(index); }

        @Override
        public final int hashCode()
        {
            int hash = 7;
            hash = 89 * hash + index;
            return hash;
        }

        @Override
        public final boolean equals(Object obj)
        {
            if(this == obj)
                return true;
            
            if(obj == null)
                return false;
            
            if (obj instanceof LineReference other)
                return index == other.index;
            
            return false;
        }
        
        @Override
        public final int compareTo(LineReference other)
        {
            return Integer.compare(index, other.index);
        }
    }
}
