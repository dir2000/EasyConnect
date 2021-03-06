package com.zhurylomihaylo.www.easyconnect;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;
import javax.swing.SwingConstants;
import javax.swing.JSeparator;

public class OptionsDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField filePath;
	private JFileChooser fileChooser;	
	private JRadioButton rdbtnAutoLocale;
	private JRadioButton rdbtnEnglishLocale;
	private JRadioButton rdbtnUkrainianLocale;

	/**
	 * Create the dialog.
	 */
	public OptionsDialog(JFrame owner) {
		super(owner, Messages.getString("OptionsDialog.Options"), true); //$NON-NLS-1$
		buildGUI();
	}

	private void restoreValues() {
		String filePathStr = Props.get("remoteProgramPath"); //$NON-NLS-1$
		if (filePathStr != null)
			filePath.setText(filePathStr);
		
		String languageTag = Props.get("languageTag"); //$NON-NLS-1$
		if (languageTag == null || languageTag.equals("auto")) //$NON-NLS-1$
			rdbtnAutoLocale.setSelected(true);
		else if (languageTag.equals("en")) //$NON-NLS-1$
			rdbtnEnglishLocale.setSelected(true);
		else if (languageTag.equals("uk")) //$NON-NLS-1$
			rdbtnUkrainianLocale.setSelected(true);
		else
			rdbtnAutoLocale.setSelected(true);
	} 
	
	private void storeValues() {
		Props.set("remoteProgramPath", filePath.getText()); //$NON-NLS-1$
		
		if (rdbtnEnglishLocale.isSelected())
			Props.set("languageTag", "en"); //$NON-NLS-1$ //$NON-NLS-2$
		else if (rdbtnUkrainianLocale.isSelected())
			Props.set("languageTag", "uk"); //$NON-NLS-1$ //$NON-NLS-2$
		else
			Props.set("languageTag", "auto"); //$NON-NLS-1$ //$NON-NLS-2$

		Messages.defineResourceBundle();
	}
	
	private ActionListener browseListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fileChooser == null) {
					fileChooser = new JFileChooser();
					FileFilter filter = new FileNameExtensionFilter(Messages.getString("OptionsDialog.ExeFile"), "exe"); //$NON-NLS-1$ //$NON-NLS-2$
					fileChooser.setFileFilter(filter);
					fileChooser.setDialogTitle(Messages.getString("OptionsDialog.SelectARemoteAccessProgram")); //$NON-NLS-1$
				}
				int result = fileChooser.showOpenDialog(OptionsDialog.this);
				if (result == JFileChooser.APPROVE_OPTION)
					filePath.setText(fileChooser.getSelectedFile().getPath());
			}
		};		
	}
	private ActionListener okListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				storeValues();
				OptionsDialog.this.setVisible(false);
			}
		};
	}
	
	private ActionListener cancelListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OptionsDialog.this.setVisible(false);
			}
		};
	}
	
	@Override
	public void setVisible(boolean b) {
		if (b)
			restoreValues();
			
		super.setVisible(b);
	}

	private void buildGUI() {
		setTitle(Messages.getString("OptionsDialog.Options")); //$NON-NLS-1$
		//setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		{
			JPanel panelRemote = new JPanel();
			contentPanel.add(panelRemote);
			GridBagLayout gbl_panelRemote = new GridBagLayout();
			gbl_panelRemote.columnWidths = new int[] {321, 400, 80, 0};
			gbl_panelRemote.rowHeights = new int[]{23, 0};
			gbl_panelRemote.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
			gbl_panelRemote.rowWeights = new double[]{0.0, Double.MIN_VALUE};
			panelRemote.setLayout(gbl_panelRemote);
			{
				JLabel lblNewLabel = new JLabel(Messages.getString("OptionsDialog.EnterThePathToRemoteAccessProgram")); //$NON-NLS-1$
				GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
				gbc_lblNewLabel.fill = GridBagConstraints.BOTH;
				gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
				gbc_lblNewLabel.gridx = 0;
				gbc_lblNewLabel.gridy = 0;
				panelRemote.add(lblNewLabel, gbc_lblNewLabel);
			}
			{
				filePath = new JTextField();
				GridBagConstraints gbc_textField = new GridBagConstraints();
				gbc_textField.fill = GridBagConstraints.BOTH;
				gbc_textField.insets = new Insets(0, 0, 0, 5);
				gbc_textField.gridx = 1;
				gbc_textField.gridy = 0;
				panelRemote.add(filePath, gbc_textField);
				filePath.setColumns(10);
			}
			{
				JButton btnBrowse = new JButton(Messages.getString("OptionsDialog.Browse")); //$NON-NLS-1$
				btnBrowse.addActionListener(browseListener());
				GridBagConstraints gbc_btnBrowse = new GridBagConstraints();
				gbc_btnBrowse.weighty = 50.0;
				gbc_btnBrowse.fill = GridBagConstraints.BOTH;
				gbc_btnBrowse.gridx = 2;
				gbc_btnBrowse.gridy = 0;
				panelRemote.add(btnBrowse, gbc_btnBrowse);
			}
		}
		{
			JPanel panelLocale = new JPanel();
			panelLocale.setBorder(new TitledBorder(null, Messages.getString("OptionsDialog.ChooseInterfaceLanguage"), TitledBorder.LEADING, TitledBorder.TOP, null, null)); //$NON-NLS-1$
			FlowLayout flowLayout = (FlowLayout) panelLocale.getLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			contentPanel.add(panelLocale);
			
			ButtonGroup group = new ButtonGroup(); 
			{
				rdbtnAutoLocale = new JRadioButton("Auto"); //$NON-NLS-1$
				group.add(rdbtnAutoLocale);
				panelLocale.add(rdbtnAutoLocale);
			}
			{
				rdbtnEnglishLocale = new JRadioButton("English"); //$NON-NLS-1$
				group.add(rdbtnEnglishLocale);
				panelLocale.add(rdbtnEnglishLocale);
			}
			{
				rdbtnUkrainianLocale = new JRadioButton("Українська"); //$NON-NLS-1$
				group.add(rdbtnUkrainianLocale);
				panelLocale.add(rdbtnUkrainianLocale);
			}			
			{
				JSeparator separator = new JSeparator();
				panelLocale.add(separator);
			}
			{
				JLabel lblDefaultLocale = new JLabel(Messages.getString("OptionsDialog.lblDefaultLocale.text") + " " + Locale.getDefault());
				panelLocale.add(lblDefaultLocale);
				lblDefaultLocale.setHorizontalAlignment(SwingConstants.LEFT);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK"); //$NON-NLS-1$
				okButton.addActionListener(okListener());
				okButton.setActionCommand("OK"); //$NON-NLS-1$
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton(Messages.getString("OptionsDialog.Cancel")); //$NON-NLS-1$
				cancelButton.addActionListener(cancelListener());
				cancelButton.setActionCommand("Cancel"); //$NON-NLS-1$
				buttonPane.add(cancelButton);
			}
		}
		pack();
	}

}
