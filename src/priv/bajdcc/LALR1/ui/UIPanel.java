package priv.bajdcc.LALR1.ui;

import priv.bajdcc.LALR1.interpret.module.ModuleUI;
import priv.bajdcc.LALR1.ui.drawing.UIGraphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * 【界面】渲染界面
 *
 * @author bajdcc
 */
public class UIPanel extends JPanel {

	private UIGraphics graphics;
	private JTextField input;
	private ModuleUI moduleUI;

	public UIPanel() {
		this.graphics = new UIGraphics(800, 600, 72, 23, 11, 25, 1);
		moduleUI = ModuleUI.getInstance();
		moduleUI.setGraphics(this.graphics);
		this.setFocusable(true);
		this.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				moduleUI.addInputChar(e.getKeyChar());
			}
		});
	}

	public UIGraphics getUIGraphics() {
		return graphics;
	}

	public void paint(Graphics g) {
		//super.paint(g);
		graphics.paint((Graphics2D) g);
	}
}