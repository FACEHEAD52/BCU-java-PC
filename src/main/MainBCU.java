package main;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import common.CommonStatic;
import common.system.fake.ImageBuilder;
import common.util.Data;
import decode.ZipLib;
import io.BCJSON;
import io.Reader;
import io.Writer;
import jogl.GLBBB;
import jogl.util.GLIB;
import page.MainFrame;
import page.MainPage;
import page.awt.AWTBBB;
import page.awt.BBBuilder;
import utilpc.UtilPC;
import utilpc.awt.PCIB;

public class MainBCU {

	public static final int ver = 40909;

	public static int FILTER_TYPE = 0;
	public static final boolean WRITE = !new File("./.project").exists();
	public static boolean preload = false, trueRun = false, loaded = false, USE_JOGL = false;

	public static String getTime() {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	}

	public static void main(String[] args) {
		trueRun = true;
		long mem = Runtime.getRuntime().maxMemory();
		if (mem >> 28 == 0) {
			Opts.pop(Opts.MEMORY, "" + (mem >> 20));
			System.exit(0);
		}
		if (!new File("./BCU_lib").exists()) {
			Opts.pop(Opts.INSTALL);
			System.exit(0);
		}

		Writer.logPrepare();
		Writer.logSetup();
		Reader.getData$0();

		ImageBuilder.builder = USE_JOGL ? new GLIB() : new PCIB();
		BBBuilder.def = USE_JOGL ? new GLBBB() : AWTBBB.INS;
		CommonStatic.def = new UtilPC.PCItr();

		new MainFrame(Data.revVer(MainBCU.ver)).initialize();
		new Timer().start();
		ZipLib.init();
		BCJSON.checkDownload();
		ZipLib.read();
		Reader.getData$1();
		loaded = true;
		MainFrame.changePanel(new MainPage());
	}

	public static String validate(String str) {
		char[] chs = new char[] { '.', '/', '\\', ':', '*', '?', '"', '<', '>', '|' };
		for (char c : chs)
			str = str.replace(c, '#');
		return str;
	}

}
