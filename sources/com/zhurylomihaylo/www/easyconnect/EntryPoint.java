package com.zhurylomihaylo.www.easyconnect;

import java.awt.EventQueue;

import javax.swing.JFrame;

public class EntryPoint {

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			MainFrame frame = new MainFrame();
			frame.setTitle("Easy connect");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		});
	}
}
