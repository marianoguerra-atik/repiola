/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package storage;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreNotFoundException;
/**
 *
 * @author mariano
 */
public class Storage {
    public static String load(String name, int recordId)
    {
        RecordStore rs;

        rs = Storage.getIfExists(name);

        if(rs == null)
        {
            return null;
        }
        else
        {
            return getRecord(rs, recordId);
        }
    }

    public static boolean save(String name, String text, int recordId)
    {
        RecordStore rs;

        rs = Storage.getIfExists(name);

        if(rs == null)
        {
            rs = Storage.create(name);
        }

        try
        {
            System.out.println("records " + rs.getNumRecords());
            if(rs.getNumRecords() < recordId)
            {
                int id = rs.addRecord(text.getBytes(), 0, text.length());
                System.out.println("added record " + id);
            }
            else
            {
                rs.setRecord(recordId, text.getBytes(), 0, text.length());
                System.out.println("set record");
            }
            return true;
        }
        catch(Exception e)
        {
            System.err.println("Could not set record\n" + e.getMessage() + e.getClass());
            return false;
        }
    }

    public static RecordStore getIfExists(String name)
    {
        RecordStore rs;
        if(name.length() > 32)
        {
            System.err.println("name too long");
            return null;
        }

        try
        {
            rs = RecordStore.openRecordStore(name, false);
            return rs;
        }
        catch(Exception e)
        {
            System.err.println("doen't exist\n" + e.getMessage());
            return null;
        }
    }

    public static RecordStore create(String name)
    {
        RecordStore rs;
        if(name.length() > 32)
        {
            System.err.println("name too long");
            return null;
        }

        try
        {
            rs = RecordStore.openRecordStore(name, true);
            return rs;
        }
        catch(Exception e)
        {
            System.err.println("Could not create record\n" + e.getMessage());
            return null;
        }
    }

    public static boolean close(RecordStore rs)
    {
        try
        {
            rs.closeRecordStore();
            return true;
        }
        catch(Exception e)
        {
            System.err.println("Could not close record\n" + e.getMessage());
            return false;
        }
    }

    public static String getRecord(RecordStore rs, int id)
    {
        byte[] record;
        try
        {
            record = rs.getRecord(id);

            if(record != null)
            {
                return (new String(record));
            }

            return null;
        }
        catch(Exception e)
        {
            System.err.println("Could not get record\n" + e.getMessage());
            return null;
        }
    }
}
