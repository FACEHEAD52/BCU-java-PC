package util.pack;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import io.InStream;
import io.OutStream;
import util.Data;
import util.system.FixIndexList;
import util.system.VImg;

public class BGStore extends FixIndexList<Background> {

	public static List<Background> getAll(Pack p) {
		List<Background> ans = new ArrayList<>();
		if (p != null) {
			ans.addAll(p.bg.getList());
			for (int rel : p.rely)
				ans.addAll(Pack.map.get(rel).bg.getList());
		} else
			for (Pack pac : Pack.map.values())
				ans.addAll(pac.bg.getList());
		return ans;
	}

	public static Background getBG(int ind) {
		int pid = ind / 1000;
		Pack pack = Pack.map.get(pid);
		if (pack == null)
			return null;
		return pack.bg.get(ind % 1000);
	}

	private Pack pack;

	protected BGStore(Pack p) {
		super(new Background[1000]);
		pack = p;
	}

	public Background add(VImg img) {
		int id = nextInd();
		String name = pack.id + Data.trio(id);
		img.name = name;
		Background bg = new Background(pack, img, id);
		add(bg);
		return bg;
	}

	public int getID(Background img) {
		int ind = indexOf(img);
		if (ind < 0 || img == null)
			ind = 0;
		return pack.id * 1000 + ind;
	}

	public String nameOf(Background img) {
		return Data.trio(indexOf(img));
	}

	@Override
	public String toString() {
		return pack.toString();
	}

	protected OutStream packup() {
		OutStream os = OutStream.getIns();
		os.writeString("0.4.0");
		Map<Integer, Background> mbg = getMap();
		os.writeInt(mbg.size());
		for (int ind : mbg.keySet()) {
			os.writeInt(ind);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ImageIO.write(mbg.get(ind).img.getImg(), "PNG", baos);
			} catch (IOException e1) {
				e1.printStackTrace();
				break;
			}
			os.writeBytesI(baos.toByteArray());

			Background bg = mbg.get(ind);
			os.writeInt(bg.top ? 1 : 0);
			os.writeInt(bg.ic);
			for (Color c : bg.cs)
				os.writeInt(c.getRGB());
		}
		os.terminate();
		return os;
	}

	protected OutStream write() {
		OutStream os = OutStream.getIns();
		os.writeString("0.4.0");
		Map<Integer, Background> mbg = getMap();
		os.writeInt(mbg.size());
		for (int ind : mbg.keySet()) {
			os.writeInt(ind);
			Background bg = mbg.get(ind);
			os.writeInt(bg.top ? 1 : 0);
			os.writeInt(bg.ic);
			for (Color c : bg.cs)
				os.writeInt(c.getRGB());
		}
		os.terminate();
		return os;
	}

	protected void zreadp(int ver, InStream is) {
		if (ver >= 309)
			ver = getVer(is.nextString());
		if (ver >= 400)
			zreadp$000400(is);
		else if (ver >= 306)
			is.nextInt();
	}

	protected void zreadt(int ver, InStream is) {
		if (ver >= 309)
			ver = getVer(is.nextString());
		if (ver >= 400)
			zreadt$000400(is);
		else if (ver >= 309)
			;
		else if (ver >= 306)
			is.nextInt();
	}

	private void zreadp$000400(InStream is) {
		int n = is.nextInt();
		for (int i = 0; i < n; i++) {
			int ind = is.nextInt();
			VImg vimg;
			ByteArrayInputStream bais = new ByteArrayInputStream(is.nextBytesI());
			try {
				BufferedImage bimg = ImageIO.read(bais);
				vimg = new VImg(bimg);
				vimg.name = Data.trio(ind);
			} catch (IOException e) {
				e.printStackTrace();
				for (int j = 0; j < 6; j++)
					is.nextInt();
				continue;
			}
			Background bg = new Background(pack, vimg, ind);
			set(ind, bg);
			bg.top = is.nextInt() > 0;
			bg.ic = is.nextInt();
			for (int j = 0; j < 4; j++)
				bg.cs[j] = new Color(is.nextInt());
		}
	}

	private void zreadt$000400(InStream is) {
		File f = new File("./res/img/" + pack.id + "/bg/");
		if (f.exists()) {
			File[] fs = f.listFiles();
			for (File fi : fs) {
				String str = fi.getName();
				if (str.length() != 7)
					continue;
				if (!str.endsWith(".png"))
					continue;
				int val = -1;
				try {
					val = Integer.parseInt(str.substring(0, 3));
				} catch (NumberFormatException e) {
					e.printStackTrace();
					continue;
				}
				BufferedImage bimg = null;
				try {
					bimg = ImageIO.read(fi);
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
				if (val >= 0 && bimg != null)
					set(val, new Background(pack, new VImg(bimg), val));
			}
		}
		int n = is.nextInt();
		for (int i = 0; i < n; i++) {
			int ind = is.nextInt();
			Background bg = get(ind);
			if (bg == null)
				continue;
			bg.top = is.nextInt() > 0;
			bg.ic = is.nextInt();
			for (int j = 0; j < 4; j++)
				bg.cs[j] = new Color(is.nextInt());
		}
	}

}