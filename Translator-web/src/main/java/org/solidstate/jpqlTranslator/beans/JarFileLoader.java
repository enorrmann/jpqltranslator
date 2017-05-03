package org.solidstate.jpqlTranslator.beans;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class JarFileLoader extends URLClassLoader
{
	public JarFileLoader (URL[] urls)
    {
        super (urls);
    }



	public void addFile (String path) throws MalformedURLException
    {
        String urlPath = "jar:file:/" + path + "!/";
        addURL (new URL (urlPath));
    }

    public static void main (String [] args)
    {
    	String clase = "modelo.personas.Persona";
        try
        {
            System.out.println ("First attempt...");
            Class.forName (clase);
        }
        catch (Exception ex)
        {
            System.out.println ("Failed.");
        }

        try
        {
            URL urls [] = {};

            JarFileLoader cl = new JarFileLoader (urls);
            String archivo ="C:/lib/entidades.jar"; 
            File f2 = new File(archivo);
            System.out.println("%%%% " + f2.exists());
            cl.addFile (archivo);
            
            
            System.out.println ("Second attempt...");
            cl.loadClass (clase);
            System.out.println ("Success!");
        }
        catch (Exception ex)
        {
            System.out.println ("Failed.");
            ex.printStackTrace ();
        }
    }
}