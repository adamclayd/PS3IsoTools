package com.ps3isotools.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridLayout;
import java.awt.Button;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.BorderLayout;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import java.awt.FlowLayout;
import java.awt.Insets;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import java.awt.CardLayout;
import java.awt.Dimension;

import net.miginfocom.swing.MigLayout;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.SpringLayout;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Color;
import javax.swing.ImageIcon;
import java.awt.Canvas;
import java.util.ArrayList;
import java.util.stream.Stream;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.awt.event.ActionEvent;
import com.ps3isotools.sfo.*;
import com.ps3isotools.ui.objects.*;
import com.ps3isotools.ui.threads.*;
import java.awt.Toolkit;

public class MakePS3ISOFrame extends JFrame implements ActionListener, ListSelectionListener, ThreadDoneListener {
	
	private JLabel isoDirsLbl;
	private JPanel addRmBtnPnl;
	private JButton addGameBtn;
	private JButton mkIsosBtn;
	private JFileChooser fc;
	private JList isoDirList;
	private JButton rmGameBtn;
	
	private ProgressDialog progressDlg;
	
	private DefaultListModel<MakePS3ISOEntry> entries;
	
	private int[] currentIndex;
	
	private File outDir;
	
	private final String PARAM_SFO = File.separator + "PS3_GAME" + File.separator + "PARAM.SFO";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MakePS3ISOFrame window = new MakePS3ISOFrame();
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MakePS3ISOFrame() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/png/icons8-iso-those-icons-lineal-color-32.png")));
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		entries = new DefaultListModel<MakePS3ISOEntry>();
		currentIndex = new int[1];
		currentIndex[0] = 0;
		fc = new JFileChooser();
		
		setTitle("Make PS3 ISOs");
		setBounds(100, 100, 600, 400);
		setMinimumSize(new Dimension(600, 400));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new MigLayout());
		
		
		
		isoDirsLbl = new JLabel("Add PS3 Game Directories");
		isoDirsLbl.setFont(new Font("Tahoma", Font.PLAIN, 18));
		isoDirsLbl.setBorder(new MatteBorder(0, 0, 0, 0, (Color) new Color(0, 0, 0)));
		getContentPane().add(isoDirsLbl, "width 60%");
		
		getContentPane().add(new JLabel(), "width 40%, wrap");
		
		isoDirList = new JList(entries);
		isoDirList.setFont(new Font("Tahoma", Font.PLAIN, 14));
		isoDirList.setVisibleRowCount(20);
		isoDirList.addListSelectionListener(this);
		
		getContentPane().add(isoDirList, "height 80%, grow");
		
		addRmBtnPnl = new JPanel();
		getContentPane().add(addRmBtnPnl, "grow,wrap");
		
		addGameBtn = new JButton("");
		addGameBtn.addActionListener(this);
		addGameBtn.setToolTipText("Add PS3 Game Directory");
		addGameBtn.setIcon(new ImageIcon(getClass().getResource("/resources/png/icons8-plus-16.png")));
		
		rmGameBtn = new JButton("");
		rmGameBtn.setToolTipText("Remove Game");
		rmGameBtn.addActionListener(this);
		rmGameBtn.setEnabled(false);
		rmGameBtn.setIcon(new ImageIcon(getClass().getResource("/resources/png/icons8-cross-mark-16.png")));
		
		GroupLayout gl_addRmBtnPnl = new GroupLayout(addRmBtnPnl);
		gl_addRmBtnPnl.setHorizontalGroup(
			gl_addRmBtnPnl.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_addRmBtnPnl.createSequentialGroup()
					.addGroup(gl_addRmBtnPnl.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(rmGameBtn, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
						.addComponent(addGameBtn, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 31, Short.MAX_VALUE))
					.addContainerGap(194, Short.MAX_VALUE))
		);
		gl_addRmBtnPnl.setVerticalGroup(
			gl_addRmBtnPnl.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_addRmBtnPnl.createSequentialGroup()
					.addGap(124)
					.addComponent(addGameBtn)
					.addGap(18)
					.addComponent(rmGameBtn)
					.addContainerGap(95, Short.MAX_VALUE))
		);
		addRmBtnPnl.setLayout(gl_addRmBtnPnl);
		
		JPanel mkIsosBtnPnl = new JPanel(); 
		getContentPane().add(mkIsosBtnPnl, "grow, width 60%");
		
		mkIsosBtn = new JButton("Make ISOs");
		mkIsosBtn.addActionListener(this);
		mkIsosBtn.setEnabled(false);
		GroupLayout gl_mkIsosBtnPnl = new GroupLayout(mkIsosBtnPnl);
		gl_mkIsosBtnPnl.setHorizontalGroup(
			gl_mkIsosBtnPnl.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_mkIsosBtnPnl.createSequentialGroup()
					.addContainerGap(225, Short.MAX_VALUE)
					.addComponent(mkIsosBtn, GroupLayout.PREFERRED_SIZE, 118, GroupLayout.PREFERRED_SIZE))
		);
		gl_mkIsosBtnPnl.setVerticalGroup(
			gl_mkIsosBtnPnl.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_mkIsosBtnPnl.createSequentialGroup()
					.addComponent(mkIsosBtn)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		mkIsosBtnPnl.setLayout(gl_mkIsosBtnPnl);
		
		
	}
	
	private boolean hasEntry(File f) {
		boolean hasEntry = false;
		for(int i = 0; i < entries.size(); i++) {
			MakePS3ISOEntry entry = entries.get(i);
			if(entry.gameDir.getAbsolutePath() == f.getAbsolutePath())
				hasEntry = true;
		}
		
		return hasEntry;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == addGameBtn) {
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setMultiSelectionEnabled(true);
			int result = fc.showOpenDialog(this);
			
			if(result == JFileChooser.APPROVE_OPTION) {
				File[] files = fc.getSelectedFiles();
				
				for(int i = 0; i < files.length; i++) {
					if(!hasEntry(files[i])) {
						File sfoFile = new File(files[i].getAbsoluteFile() + PARAM_SFO);
					
						if(sfoFile.exists()) {
							MakePS3ISOEntry entry = new MakePS3ISOEntry();
							entry.gameDir = files[i];
						
							try {
								ParamSfo sfo = new ParamSfo(sfoFile.getAbsolutePath());
								entry.title = sfo.getItemValue("TITLE");
								entry.titleId = sfo.getItemValue("TITLE_ID");
								
								entry.title = entry.title == null ? null : entry.title.trim().strip();
								entry.titleId = entry.titleId == null ? null : entry.titleId.trim().strip();
						
								entry.title = entry.title == null ? entry.gameDir.getName() : entry.title;
								entry.titleId = entry.titleId == null ? null : "[" + entry.titleId + "]";
							}
							catch(Exception ex) {
								entry.title = entry.gameDir.getName();
								entry.titleId = null;
							}
					
							entries.addElement(entry);
						}
						else {
							JOptionPane.showMessageDialog(this, files[i].getName() + " is not a valid PS3 game folder", "Invalid PS3 Game Folder", JOptionPane.ERROR_MESSAGE);
						}
					}
				
					if(entries.size() > 0)
						mkIsosBtn.setEnabled(true);
				}
			}
		}
		else if(e.getSource() == rmGameBtn) {
			entries.remove(isoDirList.getSelectedIndex());
			
			if(entries.size() <= 0 || isoDirList.getSelectedIndex() == -1) {
				rmGameBtn.setEnabled(false);
				mkIsosBtn.setEnabled(false);
			}
		}
		else if(e.getSource() == mkIsosBtn) {
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setMultiSelectionEnabled(false);
			int result = fc.showOpenDialog(this);
			
			if(result == JFileChooser.APPROVE_OPTION) {
				outDir = fc.getSelectedFile();
				
				progressDlg = new ProgressDialog(this, "Making PS3 ISOs", false);
				progressDlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
				progressDlg.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/png/icons8-iso-those-icons-lineal-color-32.png")));
				progressDlg.setVisible(true);
				setEnabled(false);
				
				startNextThread();
				
				MakePS3ISOProgressThread th = new MakePS3ISOProgressThread(progressDlg, entries, outDir, currentIndex);
				th.start();
			}
		}
	}
	


	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(!e.getValueIsAdjusting()) {
			if(isoDirList.getSelectedIndex() == -1)
				rmGameBtn.setEnabled(false);
			else
				rmGameBtn.setEnabled(true);
		}
	}

	@Override
	public void threadDone(Thread t) {
		currentIndex[0]++;
		
		if(currentIndex[0] < entries.size()) {
			startNextThread();
		}
		else {
			currentIndex[0] = 0;
			entries.clear();
			rmGameBtn.setEnabled(false);
			setEnabled(true);
			progressDlg.dispose();
			mkIsosBtn.setEnabled(false);
		}
	}
	
	private void startNextThread() {
		try {
			MakePS3ISOEntry entry = entries.get(currentIndex[0]);
			progressDlg.setStatus("Processing " + entry);
			progressDlg.setProgressStatus(currentIndex[0] + 1, entries.size(), ProgressThing.FILES);
			
			MakePS3ISOThread th = new MakePS3ISOThread(entries.get(currentIndex[0]).gameDir.getAbsolutePath(), outDir.getAbsolutePath() + File.separator + entry + ".iso");
			th.setThreadDoneListener(this);
			th.start();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
