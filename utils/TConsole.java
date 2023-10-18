/*
 * Created on Mar 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/*
 * This software is distributed under the MIT License
 *
 * Copyright (c) 2005 Alain Becam, Paulo Lopes, Joakim Olsson, and Johan Simonsson - 2005
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this 
 * software and associated documentation files (the "Software"), to deal in the Software 
 * without restriction, including without limitation the rights to use, copy, modify, 
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to 
 * permit persons to whom the Software is furnished to do so, subject to the following 
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies
 *  or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT 
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE 
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */

package utils;

import java.util.ArrayList;

import javax.swing.JFrame;

import javax.swing.JTextPane;
import javax.swing.JTextField;

import core.CommandSet;
import core.InterpretOrders;
import core.MainManager;
import core.ManageInstances;

// import mmrocom.ChannelResult;
// import mmrolink.CommManager;

// import modulesrepository.FacilityUser;
/**
 * The simple console.
 * 
 * @author Alain Becam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class TConsole extends JFrame {

	private javax.swing.JPanel jContentPane = null;

	private JTextPane jTextPane = null;

	private JTextField jTextField = null;

	private ArrayList commandHist = new ArrayList(); // Contain a limited

	// history of command

	int limitHist = 100; // Size of the historic.

	int posHist = 0; // Position in the historic.

	InterpretOrders myInterpret; // = new InterpretOrders(); // Our "command"

	// decoder

	ManageInstances myManager; // = new ManageInstances(null); // Our instance

	// manager...

	ToGetResults myGetter; // Our facility to take distant result...

	MainManager myMManager; // = new MainManager(null);

	/**
	 * This method initializes jTextPane
	 * 
	 * @return javax.swing.JTextPane
	 */
	private JTextPane getJTextPane() {
		if (jTextPane == null) {
			jTextPane = new JTextPane();
			jTextPane.doLayout();
			jTextPane.setEditable(false);
		}
		return jTextPane;
	}

	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setText("> ");
			jTextField.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyPressed(java.awt.event.KeyEvent e) {
					String resultDist;
					// Is there any distant data ???
					if (myGetter.isDataReady()) {
						// Recover the result (Ultra crappy method)
						resultDist = myGetter.getResult();
						// Then we release the getter...
						myGetter.dataConsumed();
						commandHist.add(resultDist);
					}

					if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
						if (posHist > 0) {
							posHist--;
							jTextField.setText("> "
									+ (String) commandHist.get(posHist));
						}
					}
					if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) {
						if (posHist < commandHist.size() - 1) {
							posHist++;
							System.out.println(posHist + " "
									+ commandHist.size());
							if (!commandHist.isEmpty()) {
								jTextField.setText("> "
										+ (String) commandHist.get(posHist));
							}
						}
					}
				}

				public void keyTyped(java.awt.event.KeyEvent e) {
					String resultDist;
					// Is there any distant data ???
					if (myGetter.isDataReady()) {
						// Recover the result (Ultra crappy method)
						resultDist = myGetter.getResult();
						// Then we release the getter...
						myGetter.dataConsumed();
						commandHist.add(resultDist);
					}

					if (e.getKeyChar() == '\n') {
						String line = jTextField.getText();
						String command = line.substring(2);
						StringBuffer toShow = new StringBuffer();
						String currentExc = new String(); // Contain the
						// exception, if any
						if (command.length() > 0) {
							InterpretOrders.MethodDesc myMethod;
							Object result;

							commandHist.add(command);
							try {
								myMethod = myInterpret.extractMethod(command);
								myMethod.setInstanceManager(myManager);
								result = myMethod.execMethod(0);
								commandHist.add(result.toString());
								// Distant try
								{
									CommandSet myCommandSet = new CommandSet();
									myCommandSet.setCallerName("Console");
									// We give our reference, we must implement
									// the FacilityUser interface to get a
									// result, and it is asynchrone... The
									// answer may come later...
									myCommandSet.setOwner(myGetter);
									myCommandSet.setModuleName(myMethod
											.getClassName());
									myCommandSet.setMethodName(myMethod
											.getMethodName());
									myCommandSet.setInstance(0);
									myCommandSet.setParameters(myMethod
											.getParameters());

									myMManager.getTheDispatcher()
											.ask4DistantCommand("localhost",
													myCommandSet, myGetter);
									// And then we wait for the data...
									if (myGetter.isDataReady()) {
										// Recover the result (Ultra crappy
										// method)
										resultDist = myGetter.getResult();
										// Then we release the getter...
										myGetter.dataConsumed();
										commandHist.add(resultDist);
									}
								}
							} catch (Exception ex) {
								// We do not push the Stack here !!!
								currentExc = ex.getMessage();
							}
						}
						if (commandHist.size() > limitHist) {
							commandHist.remove(0);
						}
						posHist = commandHist.size();
						for (int i = 0; i < commandHist.size(); i++) {
							toShow.append((String) commandHist.get(i) + '\n');
						}
						toShow.append(currentExc);
						jTextPane.setText(toShow.toString());
						jTextField.setText("> ");
					}
				}
			});
		}
		return jTextField;
	}

	public static void main(String[] args) {
		(new TConsole()).setVisible(true);
	}

	/**
	 * This is the default constructor
	 */
	public TConsole() {
		super();
		myGetter = new ToGetResults();
		myMManager = new MainManager(null);
		myInterpret = myMManager.getTheInterpreter();
		myManager = myMManager.getTheInstanceManager();
		/*myMManager.getTheCommManager().askDistantService("localhost",
				"getGlobalInformation");
		myMManager.getTheCommManager().askDistantService("localhost",
				"getGlobalInformation");
		myMManager.getTheCommManager().askDistantServiceWParams("localhost",
				"removeDistantModule",
				new StringBuffer("JustForTest\nlocalhost"));
		myMManager.getTheCommManager().askDistantService("localhost",
				"getGlobalInformation");
		myMManager.getTheCommManager().askDistantService("localhost",
				"getGlobalInformation");
		ExtractInterface monExtractInterface = new ExtractInterface();
		int result = monExtractInterface.recoverClass4Distant("JustForTest",
				"InterfaceForJustForTest", "localhost");
		if (result != 0) {
			// Oups...
			System.out.println("Echec de l'extraction");
		} else {
			System.out.println("Essai d'ajout de module");
			StringBuffer chunk = monExtractInterface.getTranscript();
			System.out.println("Chunk:" + chunk);
			myMManager.getTheCommManager().askDistantServiceWParams(
					"localhost", "addDistantModule", chunk);
		}
		myMManager.getTheCommManager().askDistantService("localhost",
				"getGlobalInformation");*/
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
		this.setTitle("Console");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new java.awt.BorderLayout());
			jContentPane.add(getJTextPane(), java.awt.BorderLayout.NORTH);
			jContentPane.add(getJTextField(), java.awt.BorderLayout.SOUTH);
		}
		return jContentPane;
	}
} // @jve:decl-index=0:
