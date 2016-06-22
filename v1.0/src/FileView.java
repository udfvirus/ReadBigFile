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

// WEB: http://java-virys.narod.ru
import javax.microedition.lcdui.*;

import javax.microedition.io.*;
import javax.microedition.io.file.*;
import java.io.*;

import java.util.*;


public class FileView extends Form implements CommandListener{
	
	Main midlet;
	public String filePath;
	
	Command cBack,cNext,cExit;
	
	private final FileView fv;
	
	long spos,epos;
	
	FileConnection fc = null;
	InputStream is = null;
	
	public FileView(Main m,final String _filePath)
	{
		super("Read test");
		midlet = m;
		filePath = _filePath;
		fv = this;
		spos = 0;
		epos = 200;
	}
	
	public void init()
	{
		cBack = new Command("Back", Command.ITEM, 1);
		cNext = new Command("Next", Command.ITEM, 1);
		cExit = new Command("Exit", Command.ITEM, 1);
		addCommand(cBack);
		addCommand(cNext);
		addCommand(cExit);
		setCommandListener(this);
		Thread fthread = new Thread(new Runnable(){public void run(){
			try{
				fc = 
					(FileConnection)Connector.open("file:///" + filePath,Connector.READ);
				is = fc.openInputStream();
			}catch(Exception ex){
				
			}
			readFile(filePath,spos,epos);
		}});
		fthread.start();
	}
	
	public void commandAction(Command c,Displayable d)
	{
		if(c == cBack)
		{
			epos = spos;
			spos -= 200;
			if(spos < 0)
			{
				spos = 0;
			}
			readFile(filePath,spos,epos);
		}else if(c == cNext)
		{
			spos = epos;
			epos += 200;
			readFile(filePath,spos,epos);
		}else if(c == cExit)
		{
			try{
				is.close();
				fc.close();
			}catch(Exception ex){}
			midlet.showList();
		}
	}
	
	public void readFile(final String filePath,final long _spos,final long _epos)
	{
		new Thread(new Runnable(){public void run(){
		try{
			System.out.println("commandAction.filePath: " +"file:///" + filePath);
			if(fc == null) throw new Exception("FileConnection is null");
			StringBuffer sBuf = new StringBuffer();
			int ch = 0;
			is.skip(_spos);
			for(int i = 0; i < _epos - _spos && (ch = is.read()) != -1; i++, sBuf.append((char) ch));
			System.out.println("sBuf: " + sBuf.toString());
			fv.deleteAll();
			fv.append(sBuf.toString());
		}catch(Exception ex){
			System.out.println("commandAction.readFileError: " + ex.toString());
		}
		
		}}).start();
	}
	
}
