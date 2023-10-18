// Include def
#define KEYRATE 6 // Time to wait for a new key to be added in buffer
#define G_SCALE 2.0f
#define G_MOVE 6,-7,10

// Definition of different statuts
#define PLAYER	1	// The player "alive"
#define AURA	2	// In form of Aura ball, exclusive with "alive" form
#define	EMARK	4	// With exclamation mark
#define IMARK	8	// With interogation mark
#define	STAR	128 // My beloved stars (too much polygone then...), with only x and y for now.
#define	REMOVED	1024 // I retire myself (very very rare in the MMRO design).
#define	INIT_TIME	4096 // Init time, we have to get information ONLY.
#define MOBILE_INIT 65536 // Initialisation from mobile, here for information (and test).
#define FACTION_AZA 16 // Member of Azarel
#define FACTION_PEN 32 // Member of Penemue

/************************************************************************/
/* Generic function to print messages on screen                         */
/************************************************************************/
void PrintTextMessage(char * msg);
