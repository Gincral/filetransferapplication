import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;

public class Receiver extends Thread { // 10.84.89.78
	// Receiver
	public int dataPort;
	public int ackPort;
	public InetAddress ip;
	public String filePath;
	public int ack = 0;
	public int seq = 0;
	public boolean reliable;
	public int timer;
	public int bufferSize = 1500;
	public long start;
	public static ArrayList<byte[]> bufferArray = new ArrayList<byte[]>();

	// GUI
	public JFrame frmReceiver;
	public JTextField ipTextField;
	public JTextField dataPortTextField;
	public JTextField ackPorttextField;
	public JTextField fileNametextField;
	public JTextField addresstextField;
	public File selectedFile;
	public JTextArea output;
	public JScrollPane scroll;
	private JTextField packettextField;
	private JTextField timetextField;

	public static void main(String[] args) throws IOException, NullPointerException {
		try {
			Receiver window = new Receiver();
			window.frmReceiver.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Receiver() throws UnknownHostException {
		this.ip = InetAddress.getLocalHost();
		this.dataPort = 1;
		this.ackPort = 2;
		this.bufferSize = 1500;
		this.filePath = null;
		this.timer = 1000;
		this.reliable = true;
		initialize();
	}

	public void initialize() {
		frmReceiver = new JFrame();
		frmReceiver.setResizable(false);
		frmReceiver.setBounds(100, 100, 1089, 796);
		frmReceiver.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmReceiver.getContentPane().setLayout(null);
		frmReceiver.setTitle("Receiver - 007");

		JLabel ipLabel = new JLabel("IP Address: ");
		ipLabel.setFont(new Font("Bell MT", Font.PLAIN, 20));
		ipLabel.setBounds(46, 46, 109, 20);
		frmReceiver.getContentPane().add(ipLabel);

		ipTextField = new JTextField();
		ipTextField.setBounds(214, 43, 386, 26);
		frmReceiver.getContentPane().add(ipTextField);
		ipTextField.setColumns(10);
		ipTextField.setText(this.ip.getHostAddress());

		JLabel portLabel = new JLabel("Data Transfer Port Number:");
		portLabel.setFont(new Font("Bell MT", Font.PLAIN, 20));
		portLabel.setBounds(46, 96, 285, 20);
		frmReceiver.getContentPane().add(portLabel);

		dataPortTextField = new JTextField();
		dataPortTextField.setBounds(318, 93, 153, 26);
		frmReceiver.getContentPane().add(dataPortTextField);
		dataPortTextField.setColumns(10);
		dataPortTextField.setText(Integer.toString(this.dataPort));

		JButton btnNewButton = new JButton("Start Receive");
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					output.selectAll();
					output.replaceSelection("");
					setIp(InetAddress.getByName(ipTextField.getText()));
					setDataPort(Integer.parseInt(dataPortTextField.getText()));
					setAckPort(Integer.parseInt(ackPorttextField.getText()));
					setFilePath(addresstextField.getText() + fileNametextField.getText());
					// System.out.print("freeze");
					refresh("IP Address: " + ipTextField.getText() + "\n");
					System.out.print("freeze2");
					refresh("Data Port: " + dataPortTextField.getText() + "\n");
					refresh("Ack Port: " + ackPorttextField.getText() + "\n");
					refresh("Address: " + addresstextField.getText() + fileNametextField.getText() + "\n");
					output.update(output.getGraphics());
					new Receiver2(ip, dataPort, ackPort, bufferSize, filePath, timer, reliable, output, packettextField,
							timetextField).start();
					// new Receiver().start();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(frmReceiver, "Invalid input!");
				}

			}

		});
		btnNewButton.setFont(new Font("Bell MT", Font.PLAIN, 20));
		btnNewButton.setBounds(46, 318, 983, 29);
		frmReceiver.getContentPane().add(btnNewButton);

		JButton btnFind = new JButton("Find");
		btnFind.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				int returnValue = jfc.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					selectedFile = jfc.getSelectedFile();
					addresstextField.setText(selectedFile.getAbsolutePath());
					getFileAddress(addresstextField);
				}
			}
		});
		btnFind.setFont(new Font("Bodoni MT", Font.PLAIN, 20));
		btnFind.setBounds(774, 194, 255, 29);
		frmReceiver.getContentPane().add(btnFind);

		JLabel label_1 = new JLabel(
				"\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014");
		label_1.setBounds(44, 292, 1025, 20);
		frmReceiver.getContentPane().add(label_1);

		JButton reliablebutton = new JButton("Unreliable Mode");
		reliablebutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (reliablebutton.getText().equals("Unreliable Mode")) {
					reliablebutton.setText("Reliable Mode");
					setReliable(false);
					refresh("Unreliable Mode\n");
				} else {
					reliablebutton.setText("Unreliable Mode");
					setReliable(true);
					refresh("Reliable Mode\n");
				}

			}
		});
		reliablebutton.setFont(new Font("Bodoni MT", Font.PLAIN, 20));
		reliablebutton.setBounds(774, 44, 255, 29);
		frmReceiver.getContentPane().add(reliablebutton);

		output = new JTextArea();
		output.setBackground(Color.WHITE);
		output.setEditable(false);
		output.setBounds(56, 320, 983, 376);
		output.setColumns(100);

		scroll = new JScrollPane(output);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setBounds(46, 360, 983, 335);
		frmReceiver.getContentPane().add(scroll);

		JLabel ackPort = new JLabel("Ack Transfer Port Number:");
		ackPort.setFont(new Font("Bell MT", Font.PLAIN, 20));
		ackPort.setBounds(540, 96, 285, 20);
		frmReceiver.getContentPane().add(ackPort);

		ackPorttextField = new JTextField();
		ackPorttextField.setColumns(10);
		ackPorttextField.setBounds(805, 93, 224, 26);
		frmReceiver.getContentPane().add(ackPorttextField);
		ackPorttextField.setText(Integer.toString(this.ackPort));

		JLabel lblFileAddress = new JLabel("File Name: ");
		lblFileAddress.setFont(new Font("Bell MT", Font.PLAIN, 20));
		lblFileAddress.setBounds(46, 148, 153, 20);
		frmReceiver.getContentPane().add(lblFileAddress);

		fileNametextField = new JTextField();
		fileNametextField.setColumns(10);
		fileNametextField.setBounds(214, 145, 255, 26);
		frmReceiver.getContentPane().add(fileNametextField);

		JLabel lblSizeOfUdpdatagram = new JLabel("Address To Save:");
		lblSizeOfUdpdatagram.setFont(new Font("Bell MT", Font.PLAIN, 20));
		lblSizeOfUdpdatagram.setBounds(46, 197, 224, 26);
		frmReceiver.getContentPane().add(lblSizeOfUdpdatagram);

		addresstextField = new JTextField();
		addresstextField.setColumns(10);
		addresstextField.setBounds(274, 197, 450, 26);
		// addresstextField.setEditable(false);
		frmReceiver.getContentPane().add(addresstextField);

		JLabel lblNumberOfPakect = new JLabel("Number of Pakect received: ");
		lblNumberOfPakect.setFont(new Font("Bell MT", Font.PLAIN, 20));
		lblNumberOfPakect.setBounds(46, 246, 255, 26);
		frmReceiver.getContentPane().add(lblNumberOfPakect);

		JLabel lblTimeUsed = new JLabel("Time Used:                                    ms ");
		lblTimeUsed.setFont(new Font("Bell MT", Font.PLAIN, 20));
		lblTimeUsed.setBounds(595, 246, 322, 26);
		frmReceiver.getContentPane().add(lblTimeUsed);

		packettextField = new JTextField();
		packettextField.setBackground(Color.WHITE);
		packettextField.setEditable(false);
		packettextField.setText("0");
		packettextField.setColumns(10);
		packettextField.setBounds(300, 246, 153, 26);
		frmReceiver.getContentPane().add(packettextField);

		timetextField = new JTextField();
		timetextField.setBackground(Color.WHITE);
		timetextField.setEditable(false);
		timetextField.setText("0");
		timetextField.setColumns(10);
		timetextField.setBounds(703, 246, 153, 26);
		frmReceiver.getContentPane().add(timetextField);

	}

	public void setIp(InetAddress ip) {
		this.ip = ip;
	}

	public void setDataPort(int dataPort) {
		this.dataPort = dataPort;
	}

	public void setAckPort(int ackPort) {
		this.ackPort = ackPort;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void setTimer(int timer) {
		this.timer = timer;
	}

	public static void fileCreate(String path) throws IOException {
		File file = new File(path);
		FileOutputStream out = new FileOutputStream(file);
		for (int i = 0; i < bufferArray.size(); i++) {
			out.write(bufferArray.get(i));
		}
		out.close();
	}

	public static byte[] divide(byte[] a) {
		byte[] result = new byte[a.length - 4];
		result = Arrays.copyOfRange(a, 0, a.length - 4);
		return result;
	}

	public void getFileAddress(JTextField text) {
		String str = text.getText();
		int i;
		i = str.lastIndexOf("\\") + 1;
		String newtext = str.substring(0, i);
		text.setText(newtext);
	}

	public void setReliable(boolean bool) {
		this.reliable = bool;
	}

	public void refresh(String str) {
		output.append(str);
		output.update(output.getGraphics());
	}

	public void updatetinme(String str) {
		output.append(str);
		output.update(output.getGraphics());
	}
}

class Receiver2 extends Thread {
	public InetAddress ip;
	public int dataPort;
	public int ackPort;
	public int bufferSize;
	public String filePath;
	public int timer;
	public boolean reliable;
	public JTextArea output;
	public JTextField packettextField;
	public JTextField timetextField;

	public Receiver2(InetAddress ip, int dataPort, int ackPort, int bufferSize, String filePath, int timer,
			boolean reliable, JTextArea output, JTextField packettextField, JTextField timetextField) {
		this.ip = ip;
		this.dataPort = dataPort;
		this.ackPort = ackPort;
		this.bufferSize = bufferSize;
		this.filePath = filePath;
		this.timer = timer;
		this.reliable = reliable;
		this.output = output;
		this.packettextField = packettextField;
		this.timetextField = timetextField;

	}

	@Override
	public void run() {
		try {
			ArrayList<byte[]> bufferArray = new ArrayList<byte[]>();
			int check = 1;
			int EOF = 0;
			boolean complete = false;
			refresh("Reliable Mode: " + this.reliable + "\n");

			refresh("Waiting file to receive...\n");

			// handshaking
			DatagramSocket Socket = new DatagramSocket(this.dataPort);
			byte[] handshake = new byte[4];
			DatagramPacket hs = new DatagramPacket(handshake, handshake.length);
			Socket.receive(hs);
			long start = System.nanoTime();
			refresh("Starting hand shaking.\n");
			int N = ByteBuffer.wrap(hs.getData()).getInt();
			if (N == 1) {
				refresh("Receiving hand shaking file.\n");
				byte[] handshake2 = ByteBuffer.allocate(4).putInt(1).array();
				Socket.send(new DatagramPacket(handshake2, handshake2.length, this.ip, this.ackPort));
				refresh("Hand shaking file Sent.\n");
				byte[] bsBuffer = new byte[4];
				DatagramPacket bsPacket = new DatagramPacket(bsBuffer, bsBuffer.length);
				Socket.receive(bsPacket);
				this.bufferSize = ByteBuffer.wrap(bsPacket.getData()).getInt();
				refresh("Receiving buffer size : " + this.bufferSize + "\n");
				byte[] toBuffer = new byte[4];
				DatagramPacket toPacket = new DatagramPacket(toBuffer, toBuffer.length);
				Socket.receive(toPacket);
				this.timer = ByteBuffer.wrap(toPacket.getData()).getInt();
				refresh("Receiving maximum time for wait : " + this.timer + "\n");
			} else {
				refresh("Hand shaking failed.\n");
				complete = true;
			}

			// receive data

			while (complete == false) {
				byte[] buffer = new byte[bufferSize + 4];
				DatagramPacket data = new DatagramPacket(buffer, buffer.length);
				Socket.receive(data);
				refresh("Receive packet:" + EOF + "\n");

				byte[] tempbuffer = new byte[4];
				tempbuffer = Arrays.copyOfRange(data.getData(), buffer.length - 4, buffer.length);

				if (Arrays.equals(tempbuffer, "EOFT".getBytes())) {
					complete = true;
					System.out.println("Finsih");
					refresh("Find End Of File!\n");
					break;
				} else {
					if (this.reliable == false && (EOF + 1) % 10 == 0 && check == 1) {
						System.out.println("unreliable");
						check = 0;
						refresh("Drop Packet: " + EOF + "\n");
					} else {
						EOF = ByteBuffer.wrap(tempbuffer).getInt();
						System.out.println(EOF);
						Socket.send(new DatagramPacket(tempbuffer, tempbuffer.length, this.ip, this.ackPort));
						packettextField.setText(Integer.toString(EOF + 1));
						packettextField.update(packettextField.getGraphics());
						refresh("Send Ack Number" + (EOF + 1) + "\n");
						byte[] bufferdiv = new byte[bufferSize];
						bufferdiv = divide(buffer);
						bufferArray.add(bufferdiv);
						check = 1;
					}
				}
			}

			timetextField.setText(Long.toString((System.nanoTime() - start) / 1000000));
			refresh("Socket Close.:\n");
			refresh("File Creating...\n");
			fileCreate(this.filePath, bufferArray);
			Socket.close();
			bufferArray.clear();
			refresh("Finish!\n");
			// initialize();

		} catch (SocketException e) {
			e.printStackTrace();
			// refresh("SocketException:Waiting Time Out \n ");
		} catch (IOException e) {
			e.printStackTrace();
			// refresh("IOException: Unknown Reason :p\n");
			// System.out.println("IOException: Waiting Time Out");
		}

	}

	public void refresh(String str) {
		output.append(str);
		output.update(output.getGraphics());
	}

	public static void fileCreate(String path, ArrayList<byte[]> bufferArray) throws IOException {
		File file = new File(path);
		FileOutputStream out = new FileOutputStream(file);
		for (int i = 0; i < bufferArray.size(); i++) {
			out.write(bufferArray.get(i));
		}
		out.close();
	}

	public static byte[] divide(byte[] a) {
		byte[] result = new byte[a.length - 4];
		result = Arrays.copyOfRange(a, 0, a.length - 4);
		return result;
	}
}
