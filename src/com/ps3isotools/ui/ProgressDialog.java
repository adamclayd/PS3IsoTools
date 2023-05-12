package com.ps3isotools.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import net.miginfocom.swing.MigLayout;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Frame;
import java.util.HashMap;

import com.ps3isotools.ui.objects.*;

public class ProgressDialog extends JDialog {
	
	private JProgressBar progressbar;
	private JLabel statusLbl;
	private JLabel progressLbl;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ProgressDialog dialog = new ProgressDialog(null, "ProgressDialog", false);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public ProgressDialog(Frame c, String title, boolean b) {
		super(c, title, b);
		Dimension d = new Dimension(600, 200);
		setBounds(100, 100, 600, 200);
		setMinimumSize(d);
		setMaximumSize(d);
		
		getContentPane().setLayout(new MigLayout());
		
		statusLbl = new JLabel();
		statusLbl.setFont(new Font("Tahoma", Font.PLAIN, 16));
		statusLbl.setHorizontalAlignment(SwingConstants.CENTER);
		statusLbl.setVerticalAlignment(SwingConstants.BOTTOM);
		progressbar = new JProgressBar();
		progressbar.setStringPainted(true);
		progressLbl = new JLabel();
		progressLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		progressLbl.setVerticalAlignment(SwingConstants.TOP);
		
		getContentPane().add(statusLbl, "width 100%, height 40%, wrap, grow");
		getContentPane().add(progressbar, "width 100%, height 20%, wrap, grow");
		getContentPane().add(progressLbl, "width 100%, height 40%, grow");
		
	}
	
	public void setProgress(int progress) {
		progressbar.setValue(progress);
	}
	
	public void setStatus(String status) {
		statusLbl.setText(status);
	}
	
	public void setProgressStatus(int processed, int all, ProgressThing what) {
		progressLbl.setText(processed + " of " + all + " " + what.label);
	}

}
