// Configuration variables
char * p_nick = NULL;
char * p_name = NULL;
char * p_password = NULL;
char * p_host = NULL;
char * p_faction = NULL;
int p_port;

/**
 * Load the config data from the config file
 */
void loadConfig();


/**
 * Clears from memory the config variables
 */
void clearConfig();


void loadConfig()
{
	FILE * file;

    /* Open for read (will fail if file "player.cfg" does not exist) */
    if( (file  = fopen( "player.cfg", "r" )) == NULL )
	{
		// file doesn't exist, should set default values
		return;
	}

	/* Set pointer to beginning of file: */
    fseek( file, 0L, SEEK_SET );

	char * line = new char[80];

	while(fgets(line, 80, file) != NULL)
	{
		// check if the first char is #, if it is, it's a comment
		if(line[0] != '#')
		{
			char * p;
			// format is: <name>=<value>
			p = strtok(line, "=");
			if(p != NULL)
			{
				// check which config we have just read
				if( strcmp("host", p) == 0 )
				{
					p = strtok(NULL, "\n");
					if( p != NULL )
					{
						p_host = new char[strlen(p)+1];
						strcpy(p_host, p);
					}
				}
				else if( strcmp("port", p) == 0 )
				{
					p = strtok(NULL, "\n");
					if( p != NULL )
					{
						p_port = atoi(p);
					}
				}
				else if( strcmp("password", p) == 0 )
				{
					p = strtok(NULL, "\n");
					if( p != NULL )
					{
						p_password = new char[strlen(p)+1];
						strcpy(p_password, p);
					}
				}
				else if( strcmp("nick", p) == 0 )
				{
					p = strtok(NULL, "\n");
					if( p != NULL )
					{
						p_nick = new char[strlen(p)+1];
						strcpy(p_nick, p);
					}
				}
				else if( strcmp("name", p) == 0 )
				{
					p = strtok(NULL, "\n");
					if( p != NULL )
					{
						p_name = new char[strlen(p)+1];
						strcpy(p_name, p);
					}
				}
				else if( strcmp("faction", p) == 0 )
				{
					p = strtok(NULL, "\n");
					if( p != NULL )
					{
						p_faction = new char[strlen(p)+1];
						strcpy(p_faction, p);
					}
				}
			}
		}
	}

	delete line;
	
   /* Close stream */
   if( fclose( file ) )
   {
	   // should do something if cannot close the stream
   }
}

void clearConfig()
{
	if(p_host != NULL) delete p_host;
	if(p_password != NULL) delete p_password;
	if(p_nick != NULL) delete p_nick;
	if(p_name != NULL) delete p_name;
	if(p_faction != NULL) delete p_faction;
}
