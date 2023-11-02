package DBC_Converter;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.JPasswordField;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.Color;
import java.awt.Window.Type;
import javax.swing.LayoutStyle.ComponentPlacement;

@SuppressWarnings("serial")
public class frontend extends JFrame {

	private JPanel contentPane;
	private JFrame frame;
	private JTextField path;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				frontend front = new frontend();
				front.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public frontend() {
		setFont(new Font("Tahoma", Font.BOLD, 13));
		setBackground(Color.WHITE);
		setTitle("DBC CONVERTER");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 687, 394);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);


		JButton generate = new JButton("Generate");
		generate.setFont(new Font("Tahoma", Font.BOLD, 13));
		generate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String filePath = path.getText(); 
					DBC_Convert dbc = new DBC_Convert();
					dbc.generateTheFile(filePath);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});

		String[] items = { "Intel", "Motorola" };
		JComboBox<String> comboBox = new JComboBox<>(items);
		comboBox.setFont(new Font("Tahoma", Font.BOLD, 12));

		JButton search = new JButton("Browse");
		search.setFont(new Font("Tahoma", Font.BOLD, 13));

		search.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				JFileChooser fileChooser = new JFileChooser();

				int result = fileChooser.showOpenDialog(null);

				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					String filePath = selectedFile.getAbsolutePath();
					path.setText(filePath);
					JOptionPane.showMessageDialog(frame, "File selected: " + filePath, "Success",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(frame, "No file selected.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		path = new JTextField();
		path.setFont(new Font("Tahoma", Font.BOLD, 10));
		path.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Controller Endianness");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 13));
		
		JLabel lblNewLabel = new JLabel("Input Path");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(comboBox, Alignment.LEADING, 0, 308, Short.MAX_VALUE)
						.addComponent(path, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE))
					.addGap(18)
					.addComponent(search, GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
					.addGap(52))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(277)
					.addComponent(generate, GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
					.addGap(282))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap(52, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(path, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
						.addComponent(search, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addGap(29)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addGap(31)
					.addComponent(generate, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
					.addGap(131))
		);
		contentPane.setLayout(gl_contentPane);

	}
}