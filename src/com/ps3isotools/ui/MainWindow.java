package com.ps3isotools.ui;

import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;

public class MainWindow extends JFrame implements WindowListener, ActionListener {

	private JPanel contentPane;
	private JButton extractIsosBtn;
	private JButton makeIsosBtn;
	private JFrame window;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\Adam\\eclipse-workspace\\PS3IsoTools\\resources\\png\\icons8-tools-32.png"));
		setTitle("PS3 ISO Tools");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 707, 122);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout());
		
		extractIsosBtn = new JButton("Extract PS3 ISOs");
		extractIsosBtn.setIcon(new ImageIcon("C:\\Users\\Adam\\eclipse-workspace\\PS3IsoTools\\resources\\png\\icons8-folder-32.png"));
		extractIsosBtn.setFont(new Font("Tahoma", Font.PLAIN, 26));
		extractIsosBtn.addActionListener(this);
		contentPane.add(extractIsosBtn, "height 100%, width 50%, grow");
		
		makeIsosBtn = new JButton("Make PS3 ISOs");
		makeIsosBtn.setIcon(new ImageIcon("C:\\Users\\Adam\\eclipse-workspace\\PS3IsoTools\\resources\\png\\icons8-iso-those-icons-lineal-color-32.png"));
		makeIsosBtn.setFont(new Font("Tahoma", Font.PLAIN, 26));
		makeIsosBtn.addActionListener(this);
		contentPane.add(makeIsosBtn, "width 50%, grow");
	}

	@Override
	public void windowOpened(WindowEvent e) {		
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
		setVisible(true);
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == extractIsosBtn) {
			window = new ExtractPS3ISOFrame();
			window.setVisible(true);
			window.addWindowListener(this);
			setVisible(false);
		}
		else if(e.getSource() == makeIsosBtn) {
			window = new MakePS3ISOFrame();
			window.setVisible(true);
			window.addWindowListener(this);
			setVisible(false);
		}
	}

}
