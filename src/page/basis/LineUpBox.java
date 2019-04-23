package page.basis;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Set;
import java.util.TreeSet;

import page.Page;
import util.Res;
import util.basis.Combo;
import util.basis.LineUp;
import util.system.P;
import util.system.VImg;
import util.unit.Form;
import util.unit.Unit;

public class LineUpBox extends Canvas {

	private static final long serialVersionUID = 1L;

	private Form[] backup = new Form[5];
	private final Page page;
	private LineUp lu;
	private int pt = 0, time = 0;
	private Combo sc;
	private P relative, mouse;

	protected Form sf;

	public LineUpBox(Page p) {
		page = p;
		setIgnoreRepaint(true);
	}

	@Override
	public void paint(Graphics g) {
		Image bimg = createImage(600, 300);
		if (bimg == null)
			return;
		Graphics gra = bimg.getGraphics();
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 5; j++) {
				Form f = getForm(i, j);
				VImg img;
				if (f == null)
					img = Res.slot[0];
				else
					img = f.anim.uni;
				if (sf == null || sf != f || relative == null)
					gra.drawImage(img.getImg(), 120 * j, 100 * i, null);
				if (f == null)
					continue;
				if (time == 0 && sc != null)
					for (int[] fc : sc.units)
						if (f.uid == fc[0] && f.fid >= fc[1])
							gra.drawImage(Res.slot[2].getImg(), 120 * j, 100 * i, null);
				if (time == 1 && sf != null && f.uid == sf.uid && relative == null)
					gra.drawImage(Res.slot[1].getImg(), 120 * j, 100 * i, null);
				BufferedImage lv = Res.getCost(lu.getLv(f.unit)[0], true);
				int x = 120 * j;
				int y = 100 * i + img.getImg().getHeight() - lv.getHeight();
				if (sf == null || sf != f || relative == null)
					gra.drawImage(lv, x, y, null);
			}
		if (relative != null && sf != null) {
			Point p = relative.sf(mouse).toPoint();
			BufferedImage uni = sf.anim.uni.getImg();
			gra.drawImage(uni, p.x, p.y, null);
			BufferedImage lv = Res.getCost(lu.getLv(sf.unit)[0], true);
			int x = p.x;
			int y = p.y + uni.getHeight() - lv.getHeight();
			gra.drawImage(lv, x, y, null);
		}
		g.drawImage(bimg, 0, 0, getWidth(), getHeight(), null);
		pt++;
		if (pt == 5)
			time = 1 - time;
		pt %= 5;
	}

	public void setLU(LineUp l) {
		lu = l;
		backup = new Form[5];
	}

	protected void adjForm() {
		if (sf == null)
			return;
		int i = getPos(sf);
		if (getForm(i).uid == sf.uid) {
			Form[] ufs = sf.unit.forms;
			sf = ufs[(getForm(i).fid + 1) % ufs.length];
			setForm(i, sf);
			lu.renew();
			page.callBack(null);
		}

	}

	protected void click(Point p) {
		p = getPos(p);
		select(getForm(p.y, p.x));
	}

	protected void drag(Point p) {
		if (relative == null || sf == null)
			return;
		mouse = new P(p).divide(getScale());
		int ori = getPos(sf);
		Point pf = getPos(mouse);
		int fin = pf.x + pf.y * 5;
		if (ori != fin)
			jump(ori, fin);
	}

	protected void press(Point p) {
		click(p);
		P ul = new P(getPos(p)).times(new P(120, 100));
		relative = ul.sf(mouse = new P(p).divide(getScale()));

	}

	protected void release(Point p) {
		relative = null;
		mouse = null;
	}

	protected void select(Combo c) {
		sc = c;
		time = 0;
		paint(getGraphics());
	}

	protected void select(Form f) {
		sf = f;
		if (f == null)
			return;
		if (getPos(f) == -1) {
			boolean b = false;
			for (int i = 0; i < 5; i++)
				if (backup[i] == null) {
					backup[i] = f;
					b = true;
					break;
				}
			if (!b)
				backup[4] = f;
		}
		time = 1;
		paint(getGraphics());
		page.callBack(f);
	}

	protected void setLv(int[] lv) {
		if (lv.length == 0 || sf == null)
			return;
		lu.setLv(sf.unit, sf.regulateLv(lv, lu.getLv(sf.unit)));
	}

	protected void setPos(int pos) {
		if (sf == null || getPos(sf) == -1)
			return;
		int p = getPos(sf);
		jump(p, pos);
	}

	protected void updateLU() {
		Set<Unit> su = new TreeSet<>();
		for (int i = 0; i < 10; i++)
			if (getForm(i) != null)
				su.add(getForm(i).unit);
		for (int i = 0; i < 5; i++)
			if (backup[i] != null && su.contains(backup[i].unit))
				backup[i] = null;
	}

	private Form getForm(int pos) {
		return pos < 10 ? lu.fs[pos / 5][pos % 5] : backup[pos % 5];
	}

	private Form getForm(int i, int j) {
		return i < 2 ? lu.fs[i][j] : backup[j];
	}

	private int getPos(Form f) {
		if (f == null)
			return -1;
		for (int i = 0; i < 15; i++)
			if (getForm(i) != null && getForm(i).uid == f.uid)
				return i;
		return -1;
	}

	private Point getPos(P p) {
		P ans = p.copy().times(new P(5, 3)).divide(new P(600, 300));
		ans.limit(new P(4, 2));
		return ans.toPoint();
	}

	private Point getPos(Point p) {
		P siz = new P(getSize());
		P ans = new P(p).times(new P(5, 3)).divide(siz);
		ans.limit(new P(4, 2));
		return ans.toPoint();
	}

	private P getScale() {
		return new P(getSize()).divide(new P(600, 300));
	}

	private void jump(int ior, int ifi) {
		Form f = getForm(ior);
		if (ior > ifi)
			for (int i = ifi; i <= ior; i++)
				f = setForm(i, f);
		else {
			if (ifi > 9) {
				f = setForm(ifi, f);
				if (ior > 9)
					setForm(ior, f);
				else
					for (int i = 0; i < 5; i++)
						if (backup[i] == null) {
							setForm(10 + i, f);
							break;
						}
				ifi = 9;
				f = null;
			}
			for (int i = ifi; i >= ior; i--)
				f = setForm(i, f);
		}
		lu.arrange();
		lu.renew();
		page.callBack(null);
	}

	private Form setForm(int pos, Form f) {
		Form ans = getForm(pos);
		if (pos < 10)
			lu.fs[pos / 5][pos % 5] = f;
		else
			backup[pos % 5] = f;
		return ans;
	}

}