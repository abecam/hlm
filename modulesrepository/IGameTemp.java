/*
 * Created on Sep 7, 2005
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
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface IGameTemp
{
    // Player distributed method
    public String managePos(String Nick, String RealName, float x, float y, float z, float dir, int status,
            int mindEnergy);

    // Monsters distributed methods
    public void spawnMonsters(int type, float posX, float posY, float posZ, int faction, int number);

    public String updateMonsters(float posX, float posY, float posZ);

    // End monsters distributed methods
    public void giveFaith(String IdPlayer, String IdGuru);

    public int kickOutMemberOut(String IdPlayer, String IdGuru);

    public void breakFaith(String IdPlayer);

    // End sect distributed methods
    public void ask4Spell(String creatorId, int targetType, String victimRealName, int spellType, float posX,
            float posY, float posZ, float posCreatX, float posCreatY, float posCreatZ, int nbParticipants,
            int energyGiven);

    public String updateMagic(float posX, float posY, float posZ);

    public void contributeToSpell(String creatorId, String playerId, int energyGiven, int placeWanted);

    public void cancelSpell(String creatorId, String playerId);

    public void confirmSpell(String creatorId);
}