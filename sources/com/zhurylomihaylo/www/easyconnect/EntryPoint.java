package com.zhurylomihaylo.www.easyconnect;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class EntryPoint {

	public static void main(String[] args) throws Throwable {
		EventQueue.invokeLater(() -> {
			try {
				doAllActions();
			} catch (Throwable ex) {
				JOptionPane.showMessageDialog(null, ex.getMessage());
				throw ex;
			}
		});
	}

	private static void doAllActions() {
		MainFrame frame = new MainFrame();
		frame.setTitle("Easy connect");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 500);
		frame.setVisible(true);
	}
}
