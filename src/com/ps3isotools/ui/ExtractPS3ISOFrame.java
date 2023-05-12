package com.ps3isotools.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.DefaultListModel;

import net.miginfocom.swing.MigLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.ps3isotools.sfo.ParamSfo;
import com.ps3isotools.ui.objects.*;
import com.ps3isotools.ui.threads.*;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.ImageIcon;

import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.SevenZipNativeInitializationException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Toolkit;

public class ExtractPS3ISOFrame extends JFrame implements ActionListener, ListSelectionListener, ThreadDoneListener {
	private JLabel isosLbl;
	private JList isosList;
	private JPanel addRmIsoBtnPnl;
	private JFileChooser fc;
	private JButton addIsoBtn;
	private JButton rmIsoBtn;
	private JPanel extractIsosBtnPnl;
	private DefaultListModel<ExtractPS3ISOEntry> entries;
	private JButton extractIsosBtn;
	private int[] currentIndex;
	private ProgressDialog dlg;
	private File outDir;
	
	class FileTypeFilter extends FileFilter {
		public String ext;
		public String desc;
		
		public FileTypeFilter(String ext, String desc) {
			this.ext = ext;
			this.desc = desc;
		}
		
		public boolean accept(File f) {
			boolean r = f.getName().endsWith("." + ext);
			if(f.isDirectory())
				r = true;
			
			return r;
		}
		
		public String getDescription() {
			return desc + String.format(" (*.%s)", ext);
		}
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ExtractPS3ISOFrame frame = new ExtractPS3ISOFrame();
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
	public ExtractPS3ISOFrame() {
		currentIndex = new int[] {0};
		
		setTitle("Extract PS3 ISOs");
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/png/icons8-folder-32.png")));
		fc = new JFileChooser();
		
		setResizable(false);
		setBounds(100, 100, 758, 474);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new MigLayout());
		
		isosLbl = new JLabel("Select PS3 ISOs");
		isosLbl.setFont(new Font("Tahoma", Font.PLAIN, 22));
		getContentPane().add(isosLbl, "width 60%,grow");
		getContentPane().add(new JLabel(), "width 40%,wrap");
		
		entries = new DefaultListModel<ExtractPS3ISOEntry>();
		isosList = new JList(entries);
		isosList.addListSelectionListener(this);
		isosList.setVisibleRowCount(20);
		isosList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		getContentPane().add(isosList, "width 60%,height 80%,grow");
		
		addRmIsoBtnPnl = new JPanel();
		getContentPane().add(addRmIsoBtnPnl, "width 60%,grow,wrap");
		
		addIsoBtn = new JButton("");
		addIsoBtn.addActionListener(this);
		addIsoBtn.setToolTipText("Add PS3 ISO");
		addIsoBtn.setIcon(new ImageIcon(getClass().getResource("/resources/png/icons8-plus-16.png")));
		
		rmIsoBtn = new JButton("");
		rmIsoBtn.addActionListener(this);
		rmIsoBtn.setToolTipText("Remove PS3 ISO");
		rmIsoBtn.setEnabled(false);
		rmIsoBtn.setIcon(new ImageIcon(getClass().getResource("/resources/png/icons8-cross-mark-16.png")));
		GroupLayout gl_addRmIsoBtnPnl = new GroupLayout(addRmIsoBtnPnl);
		gl_addRmIsoBtnPnl.setHorizontalGroup(
			gl_addRmIsoBtnPnl.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_addRmIsoBtnPnl.createSequentialGroup()
					.addGroup(gl_addRmIsoBtnPnl.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(rmIsoBtn, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
						.addComponent(addIsoBtn, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 36, Short.MAX_VALUE))
					.addContainerGap(287, Short.MAX_VALUE))
		);
		gl_addRmIsoBtnPnl.setVerticalGroup(
			gl_addRmIsoBtnPnl.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_addRmIsoBtnPnl.createSequentialGroup()
					.addGap(147)
					.addComponent(addIsoBtn)
					.addGap(18)
					.addComponent(rmIsoBtn)
					.addContainerGap(139, Short.MAX_VALUE))
		);
		addRmIsoBtnPnl.setLayout(gl_addRmIsoBtnPnl);
		
		extractIsosBtnPnl = new JPanel();
		getContentPane().add(extractIsosBtnPnl, "width 60%,grow");
		
		extractIsosBtn = new JButton("Extract ISOs");
		extractIsosBtn.addActionListener(this);
		extractIsosBtn.setEnabled(false);
		GroupLayout gl_extractIsosBtnPnl = new GroupLayout(extractIsosBtnPnl);
		gl_extractIsosBtnPnl.setHorizontalGroup(
			gl_extractIsosBtnPnl.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_extractIsosBtnPnl.createSequentialGroup()
					.addContainerGap(274, Short.MAX_VALUE)
					.addComponent(extractIsosBtn))
		);
		gl_extractIsosBtnPnl.setVerticalGroup(
			gl_extractIsosBtnPnl.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_extractIsosBtnPnl.createSequentialGroup()
					.addComponent(extractIsosBtn)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		extractIsosBtnPnl.setLayout(gl_extractIsosBtnPnl);
	}
	
	private void readSfo(String archiveFile) throws Exception {
		boolean exists = false;
		for(int i = 0; i < entries.size(); i++) {
			if(entries.get(i).iso.getAbsolutePath().compareTo(archiveFile) == 0)
				exists = true;
		}
		
		if(!exists) {
			IInArchive archive = null;
			RandomAccessFile raf = null;
			ParamSfo sfo = null;
		
			File exdir = new File(System.getProperty("java.io.tmpdir") + File.separator + "ps3isotools");
		
			if(!exdir.exists())
				exdir.mkdir();
		
			File f = new File(exdir.getAbsoluteFile() + File.separator + "PARAM.SFO");
		
			try {
				raf = new RandomAccessFile(archiveFile, "r");
    		
				archive = SevenZip.openInArchive(ArchiveFormat.ISO, new RandomAccessFileInStream(raf));
				ISimpleInArchive sarchive = archive.getSimpleInterface();
			
				boolean sfoFound = false;
				for(int i = 0; i < archive.getNumberOfItems(); i++) {
					if(sarchive.getArchiveItem(i).getPath().compareTo("PS3_GAME" + File.separator + "PARAM.SFO") == 0) {
						sfoFound = true;
					
						sarchive.getArchiveItem(i).extractSlow(new ISequentialOutStream() {
							public int write(byte[] data) throws SevenZipException {
								try(FileOutputStream out = new FileOutputStream(f)) {
									out.write(data);
								}
								catch(IOException ex) {
									ex.printStackTrace();
								}
							
								return data.length;
							}
						});
					}
				}
			
				if(!sfoFound) {
					throw new NotAPS3GameException(archiveFile + " not a PS3 game");
				}
			
				sfo = new ParamSfo(f.getAbsolutePath());
				ExtractPS3ISOEntry entry = new ExtractPS3ISOEntry();
				entry.iso = new File(archiveFile);
				entry.title = sfo.getItemValue("TITLE").trim().strip();
				entry.titleId = sfo.getItemValue("TITLE_ID").trim().strip();
				entries.addElement(entry);
			}
			catch (Exception e) {
				if(archive != null)
					archive.close();
			
				if(raf != null)
					raf.close();
			
				if(sfo != null)
					sfo.close();
			
				throw e;
			}
			finally {
				archive.close();
				raf.close();
				sfo.close();
			
				if(f.exists())
					f.delete();
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == addIsoBtn) {
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setMultiSelectionEnabled(true);
			fc.setFileFilter(new FileTypeFilter("iso", "Iso Files"));
			
			int result = fc.showOpenDialog(this);
			if(result == JFileChooser.APPROVE_OPTION) {
				File[] files = fc.getSelectedFiles();
				
				for(int i = 0; i < files.length; i++) {
					try {
						readSfo(files[i].getAbsolutePath());
						extractIsosBtn.setEnabled(true);
					}
					catch (Exception ex) {
						JOptionPane.showMessageDialog(this, ex.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
		else if(e.getSource() == rmIsoBtn) {
			entries.remove(isosList.getSelectedIndex());
			isosList.setSelectedIndex(-1);
			rmIsoBtn.setEnabled(false);
			
			if(entries.size() <= 0)
				extractIsosBtn.setEnabled(false);
		}
		else if(e.getSource() == extractIsosBtn) {
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setMultiSelectionEnabled(false);
			
			int result = fc.showOpenDialog(this);
			if(result == JFileChooser.APPROVE_OPTION) {
				outDir = fc.getSelectedFile();
				
				dlg = new ProgressDialog(this, "Extracting PS3 ISOs",  false);
				dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
				dlg.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/png/icons8-folder-32.png")));
				dlg.setVisible(true);
				setEnabled(false);
				
				startNextThread();
				
				ExtractPS3ISOProgressThread t = new ExtractPS3ISOProgressThread(dlg, entries, outDir, currentIndex);
				t.start();
			}
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(!isosList.getValueIsAdjusting()) {
			if(isosList.getSelectedIndex() == -1)
				rmIsoBtn.setEnabled(false);
			
			else
				rmIsoBtn.setEnabled(true);
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
			isosList.setSelectedIndex(-1);
			rmIsoBtn.setEnabled(false);
			setEnabled(true);
			dlg.dispose();
			extractIsosBtn.setEnabled(false);
		}
	}
	
	private void startNextThread() {
		try {
			ExtractPS3ISOEntry entry = entries.get(currentIndex[0]);
			dlg.setStatus("Processing " + entry);
			dlg.setProgressStatus(currentIndex[0] + 1, entries.size(), ProgressThing.FILES);
			
			ExtractPS3ISOThread th = new ExtractPS3ISOThread(entry.iso.getAbsolutePath(), outDir.getAbsolutePath() + File.separator + entry);
			th.setThreadDoneListener(this);
			th.start();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}
