/*
 * Created on Feb 4, 2005
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

package modulesrepository;

/**
 * @author Alain Becam
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class maClasseDeTest implements TestInterface {

	/* (non-Javadoc)
	 * @see mmrolink.TestInterface#maMethodAMoi(java.lang.String, int, char)
	 */
	public int maMethodAMoi(String bidule, Integer truc, Character trucmuch) {
		// TODO Auto-generated method stub
		System.out.println("Je suis"+bidule);
		return 0;
	}
	
	public int maMethodAMoi2(String bidule, int truc, char trucmuch) {
		// TODO Auto-generated method stub
		System.out.println("Je suis"+bidule);
		return 0;
	}

	/* (non-Javadoc)
	 * @see mmrolink.TestInterface#monAutreMethodAMoi(int, java.lang.String, int, java.lang.String)
	 */
	public String monAutreMethodAMoi(int EhOh, String JeSuisBo, int IAmlast,
			String JsuisPasBo) {
		// TODO Auto-generated method stub
		System.out.println("Est-ce que je suis bô ? "+ JeSuisBo);
		return (JeSuisBo+JsuisPasBo);
	}

	/* (non-Javadoc)
	 * @see mmrolink.TestInterface#maDerniereMethode(java.lang.String)
	 */
	public void maDerniereMethode(String nomDuFilm) {
		// TODO Auto-generated method stub
		System.out.println("Je veux rien dire !!!!");

	}

	public static void main(String[] args) {
		// On fait quedalle !!!!
	}
	
	public int maMethodDummy(String JeSersARien)
	{
		System.out.println("Je suis là pour pas être vue, pour pas être utilisée, mais au fait, pourquoi j'existe ?");
		return 0;
	}
}
