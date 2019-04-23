package event;

import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TreeMap;

import page.MainLocale;
import util.stage.MapColc;

public strictfp class Namer {

	private static final ResourceBundle[] ERES, GRES, IRES, LRES;

	public static final Map<Integer, String> EMAP, GMAP, IMAP;

	private static final ResourceBundle RGEN = ResourceBundle.getBundle("event.gacha_en");

	static {
		String s0 = "event.";
		String[] s1 = new String[] { "event", "group", "item", "lang" };
		String[] s2 = new String[] { "_en", "_zh", "_en", "_en" };
		ResourceBundle[][] ress = new ResourceBundle[s1.length][s2.length];
		for (int i = 0; i < s1.length; i++)
			for (int j = 0; j < s2.length; j++)
				ress[i][j] = ResourceBundle.getBundle(s0 + s1[i] + s2[j]);
		ERES = ress[0];
		GRES = ress[1];
		IRES = ress[2];
		LRES = ress[3];
		EMAP = new TreeMap<>();
		GMAP = new TreeMap<>();
		IMAP = new TreeMap<>();
	}

	public static String get(String key) {
		try {
			return LRES[MainLocale.lang].getString(key);
		} catch (MissingResourceException e) {
			return "!" + key + "!";
		}
	}

	public static String getE(int id) {
		try {
			if (id / 1000 == 1)
				return MapColc.getMap(id).toString();
			else
				return ERES[MainLocale.lang].getString("" + id);
		} catch (MissingResourceException e) {
			if (!EMAP.containsKey(id))
				if (id / 1000 == 2)
					return getE(-2000) + " ID= " + id;
				else if (id / 2000 == 4)
					return getE(-8000) + " ID= " + id;
				else
					return "ID=" + id;
			else
				return EMAP.get(id);
		}

	}

	public static String getEG(String name) {
		try {
			return GRES[MainLocale.lang].getString(name);
		} catch (MissingResourceException e) {
			return "!" + name + "!";
		}

	}

	public static String getEPure(int id) {
		try {
			if (id / 1000 == 1)
				return MapColc.getMap(id).toString();
			else if (id / 1000 != 0 && id / 1000 != 3)
				return ERES[MainLocale.lang].getString("" + id);
			return "";
		} catch (MissingResourceException e) {
			if (!EMAP.containsKey(id))
				return "";
			else
				return EMAP.get(id);
		}
	}

	public static String getG(int id) {
		if (GMAP.containsKey(id))
			return getG(GMAP.get(id));
		else
			return "ID=" + id;
	}

	public static String getG(String key) {
		try {
			return RGEN.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	public static String getI(int id) {
		try {
			return IRES[MainLocale.lang].getString("" + id);
		} catch (MissingResourceException e) {
			if (IMAP.containsKey(id))
				return IMAP.get(id);
			else if (id > 10000 && id < 11000)
				if (GMAP.containsKey(id - 10000))
					return getG(id - 10000);
				else
					return getI(-10000);
			else if (id > 11000 && id < 12000)
				return getI(-11000) + " ID=" + id;
			else
				return "ID=" + id;
		}
	}

}