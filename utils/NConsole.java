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



import javax.swing.JTextPane;
import javax.swing.JTextField;

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
public class NConsole  {

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

	public static void main(String[] args) {
		//(new TConsole()).setVisible(true);
		new NConsole();
	}

	/**
	 * This is the default constructor
	 */
	public NConsole() {
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
//		this.setSize(300, 200);
//		this.setContentPane(getJContentPane());
//		this.setTitle("Console");
	}
} // @jve:decl-index=0:
