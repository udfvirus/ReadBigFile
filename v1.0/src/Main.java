/*
	Copyright 2016 Sychov Vitaliy

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
// io
import javax.microedition.io.*;
import javax.microedition.io.file.*;
import java.io.*;

import java.util.*;

public class Main extends MIDlet implements CommandListener{
	
	Display dsp;
	List list;
	boolean isRoot;
	String fspath = "";
	Command cExit,cAbout;
	public void startApp()
	{
		System.out.println("startApp");
		dsp = Display.getDisplay(this);
		list = new List("",Choice.IMPLICIT);
		list.setCommandListener(this);
		cExit = new Command("Exit", Command.ITEM, 1);
		cAbout = new Command("About", Command.ITEM, 1);
		list.addCommand(cExit);
		list.addCommand(cAbout);
		showRoot();
		dsp.setCurrent(list);
	}
	
	public void pauseApp()
	{
		System.out.println("pauseApp");
	}
	
	public void destroyApp(boolean flag)
	{
		System.out.println("destroyApp");
		System.gc();
		notifyDestroyed();
	}
	
	public void showList()
	{
		dsp.setCurrent(list);
	}
	
	public void commandAction(Command c,Displayable d)
	{
		if(c == List.SELECT_COMMAND)
		{
			int index = list.getSelectedIndex();
			if(index <= 0 && !isRoot)
			{
				System.out.println("commandAction.index: " + index);
				int spos = fspath.lastIndexOf('/',fspath.length() - 2);
				if(spos > -1)
				{
					System.out.println("FSPATH:"+fspath.substring(0,spos + 1));
					fspath = fspath.substring(0,spos + 1);
					showDir(fspath);
				}else {
					fspath = "";
					showRoot();
				}
				
			}else if(list.getString(index).endsWith("/"))
			{
				showDir(fspath += list.getString(index));
			}else {
				// Здесь будет открытие большого файла
				FileView fv = new FileView(this,fspath + list.getString(index));
				fv.init();
				dsp.setCurrent(fv);
			}
		}else if(c == cExit)
		{
			destroyApp(true);
		}else if(c == cAbout)
		{
			Alert  about = new Alert("About","This program is an example of large files are opened. \n web1: http://srcblog.ru \n web2: java-virys.narod.ru \n Vendor: javavirys",null,null);
			about.setTimeout(4000);
			dsp.setCurrent(about);
		}
	}
	
	public void showRoot()
	{
		new Thread(new Runnable(){public void run(){
			isRoot = true;
			list.deleteAll();
			Enumeration e = listRoots();
			if(e == null)
			{
				list.append("null",null);
				return;
			}
			for(; e.hasMoreElements(); list.append((String)e.nextElement(),null));
		}}).start();
	}
	
	public void showDir(final String _path)
	{
		System.out.println("showDir._path: "+_path);
		
		new Thread(new Runnable(){public void run(){
			isRoot = false;
			list.deleteAll();
			Enumeration e = list(_path,true);
			list.append("...",null);
			for(; e.hasMoreElements(); list.append((String)e.nextElement(),null));
		}}).start();
	}
	
	public static Enumeration listRoots()
	{
		try
		{
			return FileSystemRegistry.listRoots();
		}
		catch(Exception e)
		{
			//ErrScreen.showErrMsg(110, e);
			return null; // (new Vector()).elements();
		}
	}
	
	public static Enumeration list(String folder, boolean includeHidden)
	{
		Enumeration files = null;

		try
		{
			FileConnection fc = (FileConnection)Connector.open("file:///" + folder, Connector.READ);
			files = fc.list("*", includeHidden);
			fc.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return files;
	}

	
}
