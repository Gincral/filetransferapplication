import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
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

public class Sender extends Thread {
	// sender
	public int dataPort;
	public int ackPort;
	public InetAddress ip;
	public String filePath;
	public int ack = 0;
	public int seq = 0;
	public int bufferSize = 1500;
	// public static ArrayList<byte[]> bufferArray = new ArrayList<byte[]>();
	public int timer;
	public long start;

	// GUI
	public JFrame frame;
	public JTextField ipTextField;
	public JTextField dataPortTextField;
	public JTextField ackPorttextField;
	public JTextField addresstextField;
	public JTextField UDPSizetextField;
	public JTextField TimeOuttextField;
	public File selectedFile;
	public JTextArea output;
	public JScrollPane scroll;
	public JLabel packetlabel;
	private JTextField packettextField;
	private JTextField timetextField;

	public static void main(String[] args) throws IOException, NullPointerException {
		try {
			Sender window = new Sender();
			window.frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Sender() throws UnknownHostException {
		this.ip = InetAddress.getLocalHost();
		this.dataPort = 1;
		this.ackPort = 2;
		this.bufferSize = 1500;
		this.filePath = null;
		this.timer = 1000;
		initialize();
	}

	public void initialize() throws UnknownHostException {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 1089, 796);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setTitle("Sender - 007");

		JLabel ipLabel = new JLabel("IP address: ");
		ipLabel.setFont(new Font("Bell MT", Font.PLAIN, 20));
		ipLabel.setBounds(46, 46, 109, 20);
		frame.getContentPane().add(ipLabel);

		ipTextField = new JTextField();
		ipTextField.setBounds(214, 43, 386, 26);
		frame.getContentPane().add(ipTextField);
		ipTextField.setColumns(10);
		ipTextField.setText(this.ip.getHostAddress());

		JLabel portLabel = new JLabel("Data Transfer Port Number:");
		portLabel.setFont(new Font("Bell MT", Font.PLAIN, 20));
		portLabel.setBounds(46, 96, 285, 20);
		frame.getContentPane().add(portLabel);

		dataPortTextField = new JTextField();
		dataPortTextField.setBounds(318, 93, 153, 26);
		frame.getContentPane().add(dataPortTextField);
		dataPortTextField.setColumns(10);
		dataPortTextField.setText(Integer.toString(this.dataPort));

		JLabel ack_Port = new JLabel("Ack Transfer Port Number:");
		ack_Port.setFont(new Font("Bell MT", Font.PLAIN, 20));
		ack_Port.setBounds(540, 96, 285, 20);
		frame.getContentPane().add(ack_Port);

		ackPorttextField = new JTextField();
		ackPorttextField.setColumns(10);
		ackPorttextField.setBounds(805, 93, 224, 26);
		frame.getContentPane().add(ackPorttextField);
		ackPorttextField.setText(Integer.toString(this.ackPort));

		JButton btnNewButton = new JButton("Transfer");
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					output.selectAll();
					output.replaceSelection("");
					setIp(InetAddress.getByName(ipTextField.getText()));
					setDataPort(Integer.parseInt(dataPortTextField.getText()));
					setAckPort(Integer.parseInt(ackPorttextField.getText()));
					setBufferSize(Integer.parseInt(UDPSizetextField.getText()));
					setTimer(Integer.parseInt(TimeOuttextField.getText()));
					setFilePath(addresstextField.getText());

					refresh("IP Address: " + ipTextField.getText() + "\n");
					refresh("Data Port: " + dataPortTextField.getText() + "\n");
					refresh("Ack Port: " + ackPorttextField.getText() + "\n");
					refresh("Buffer Szie: " + UDPSizetextField.getText() + "\n");
					refresh("Time Set: " + TimeOuttextField.getText() + "\n");
					refresh("File Address: " + addresstextField.getText() + "\n");
					new Sender2(ip, dataPort, ackPort, bufferSize, filePath, timer, output, packettextField,
							timetextField).start();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(frame, "Invalid input!");
				}
			}

		});
		btnNewButton.setFont(new Font("Bell MT", Font.PLAIN, 20));
		btnNewButton.setBounds(46, 298, 983, 29);
		frame.getContentPane().add(btnNewButton);

		JButton btnFind = new JButton("Find");
		btnFind.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				int returnValue = jfc.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					selectedFile = jfc.getSelectedFile();
					addresstextField.setText(selectedFile.getAbsolutePath());
				}
			}
		});

		btnFind.setFont(new Font("Bodoni MT", Font.PLAIN, 20));
		btnFind.setBounds(774, 143, 255, 29);
		frame.getContentPane().add(btnFind);

		JLabel label_1 = new JLabel(
				"\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014\u2014");
		label_1.setBounds(46, 276, 1025, 20);
		frame.getContentPane().add(label_1);

		output = new JTextArea();
		output.setBackground(Color.WHITE);
		output.setEditable(false);
		output.setBounds(46, 340, 983, 376);
		output.setColumns(100);

		scroll = new JScrollPane(output);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setBounds(46, 340, 983, 376);
		frame.getContentPane().add(scroll);

		JLabel lblFileAddress = new JLabel("File address: ");
		lblFileAddress.setFont(new Font("Bell MT", Font.PLAIN, 20));
		lblFileAddress.setBounds(46, 148, 153, 20);
		frame.getContentPane().add(lblFileAddress);

		addresstextField = new JTextField();
		addresstextField.setColumns(10);
		addresstextField.setBounds(214, 145, 524, 26);
		frame.getContentPane().add(addresstextField);

		JLabel lblSizeOfUdpdatagram = new JLabel("Size of UDP datagram:");
		lblSizeOfUdpdatagram.setFont(new Font("Bell MT", Font.PLAIN, 20));
		lblSizeOfUdpdatagram.setBounds(46, 197, 224, 26);
		frame.getContentPane().add(lblSizeOfUdpdatagram);

		UDPSizetextField = new JTextField();
		UDPSizetextField.setColumns(10);
		UDPSizetextField.setBounds(274, 197, 186, 26);
		frame.getContentPane().add(UDPSizetextField);
		UDPSizetextField.setText(Integer.toString(this.bufferSize));

		JLabel lblIntegerNumberFor = new JLabel("Integer number for time out:");
		lblIntegerNumberFor.setFont(new Font("Bell MT", Font.PLAIN, 20));
		lblIntegerNumberFor.setBounds(494, 197, 252, 26);
		frame.getContentPane().add(lblIntegerNumberFor);

		TimeOuttextField = new JTextField();
		TimeOuttextField.setColumns(10);
		TimeOuttextField.setBounds(774, 197, 207, 26);
		frame.getContentPane().add(TimeOuttextField);
		TimeOuttextField.setText(Integer.toString(this.timer));

		JLabel lblMs = new JLabel("ms");
		lblMs.setFont(new Font("Bell MT", Font.PLAIN, 20));
		lblMs.setBounds(986, 197, 40, 26);
		frame.getContentPane().add(lblMs);

		JLabel label7 = new JLabel("Time Using:                               ms");
		label7.setFont(new Font("Bell MT", Font.PLAIN, 20));
		label7.setBounds(540, 250, 329, 21);
		frame.getContentPane().add(label7);

		JLabel lblPacketsSentMs = new JLabel("Packets Sent: ");
		lblPacketsSentMs.setFont(new Font("Bell MT", Font.PLAIN, 20));
		lblPacketsSentMs.setBounds(46, 250, 142, 21);
		frame.getContentPane().add(lblPacketsSentMs);

		packettextField = new JTextField();
		packettextField.setBackground(Color.WHITE);
		packettextField.setEditable(false);
		packettextField.setText("0");
		packettextField.setColumns(10);
		packettextField.setBounds(194, 247, 153, 26);
		frame.getContentPane().add(packettextField);

		timetextField = new JTextField();
		timetextField.setBackground(Color.WHITE);
		timetextField.setEditable(false);
		timetextField.setText("0");
		timetextField.setColumns(10);
		timetextField.setBounds(652, 247, 142, 26);
		frame.getContentPane().add(timetextField);

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

	public void refresh(String str) {
		output.append(str);
		output.update(output.getGraphics());
	}

}

class Sender2 extends Thread {
	public InetAddress ip;
	public int dataPort;
	public int ackPort;
	public int bufferSize;
	public String filePath;
	public int timer;
	public JTextArea output;
	private JTextField packettextField;
	private JTextField timetextField;

	public Sender2(InetAddress ip, int dataPort, int ackPort, int bufferSize, String filePath, int timer,
			JTextArea output, JTextField packettextField, JTextField timetextField) {
		this.ip = ip;
		this.dataPort = dataPort;
		this.ackPort = ackPort;
		this.bufferSize = bufferSize;
		this.filePath = filePath;
		this.timer = timer;
		this.output = output;
		this.packettextField = packettextField;
		this.timetextField = timetextField;
	}

	@Override
	public void run() {
		try {
			int ack = 0;
			int seq = 0;
			ArrayList<byte[]> bufferArray = new ArrayList<byte[]>();
			long start = System.nanoTime();
			DatagramSocket Socket = new DatagramSocket(this.ackPort);
			byte[] handshake = ByteBuffer.allocate(4).putInt(1).array();
			refresh("Starting hand shaking. \n");
			Socket.send(new DatagramPacket(handshake, handshake.length, this.ip, this.dataPort));
			byte[] handshake2 = new byte[4];
			DatagramPacket hs2 = new DatagramPacket(handshake2, handshake2.length);
			Socket.receive(hs2);
			int N = ByteBuffer.wrap(hs2.getData()).getInt();
			if (N == 1) {
				byte[] BufferSize = ByteBuffer.allocate(4).putInt(bufferSize).array();
				Socket.send(new DatagramPacket(BufferSize, BufferSize.length, this.ip, this.dataPort));
				byte[] TimeOut = ByteBuffer.allocate(4).putInt(this.timer).array();
				Socket.send(new DatagramPacket(TimeOut, TimeOut.length, this.ip, this.dataPort));
			}
			refresh("Hand shaking Success! \n");

			// send data
			fileSeperate(this.filePath, this.bufferSize, bufferArray);
			boolean complete = false;

			while (complete == false) {
				refresh("Sending Packet " + seq + "\n");
				// send packet
				Socket.send(
						new DatagramPacket(bufferArray.get(seq), bufferArray.get(seq).length, this.ip, this.dataPort));

				byte[] tempbuffer = new byte[4];
				tempbuffer = Arrays.copyOfRange(bufferArray.get(seq), bufferArray.get(seq).length - 4,
						bufferArray.get(seq).length);
				if (Arrays.equals(tempbuffer, "EOFT".getBytes())) {
					System.out.println("Finish");
					complete = true;
					refresh("Sending finished! \n");
					break;
				}
				// receive ack
				try {
					Socket.setSoTimeout(this.timer);
					byte[] buffer = new byte[4];
					DatagramPacket ackPacket = new DatagramPacket(buffer, buffer.length);
					Socket.receive(ackPacket);
					ack = ByteBuffer.wrap(ackPacket.getData()).getInt();
					seq = ack + 1;
					refresh("Receive ack number: " + seq + "\n");
				} catch (Exception e) {
					System.out.println("time out with ack: " + ack);
					refresh("time out with ack: " + ack + " \n");
				}
				packettextField.setText(Integer.toString(seq));
				packettextField.update(packettextField.getGraphics());

			}
			timetextField.setText(Long.toString((System.nanoTime() - start) / 1000000));
			bufferArray.clear();
			// send data
			Socket.close();
			System.out.print("this step");
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void refresh(String str) {
		output.append(str);
		output.update(output.getGraphics());
	}

	public static void fileSeperate(String path, int bufferSize, ArrayList<byte[]> bufferArray) throws IOException {
		FileInputStream in = new FileInputStream(new File(path));
		int count = 0;
		while (in.available() > 0) {
			byte[] ACKbuffer = ByteBuffer.allocate(4).putInt(count).array();
			byte[] databuffer = new byte[bufferSize];
			in.read(databuffer);
			byte[] packetbuffer = new byte[bufferSize + 4];
			packetbuffer = combine(databuffer, ACKbuffer);
			bufferArray.add(packetbuffer);
			count++;
		}
		byte[] eofPacket = new byte[bufferSize + 4];
		byte[] nullbuffer = new byte[bufferSize];

		String str = "EOFT";
		byte[] ack = str.getBytes();

		eofPacket = combine(nullbuffer, ack);
		bufferArray.add(eofPacket);

	}

	public static byte[] combine(byte[] a, byte[] b) {
		byte[] result = new byte[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}
}
