package ca.mb.armchair.rel3.dbrowser.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import ca.mb.armchair.rel3.client.crash.CrashTrap;
import ca.mb.armchair.rel3.client.string.ClientFromURL;
import ca.mb.armchair.rel3.client.string.StringReceiverClient;
import ca.mb.armchair.rel3.dbrowser.version.Version;

public class Backup {

	public static String getSuggestedBackupFileName(String dbURL) {
		String fname;
		if (dbURL.startsWith("local:"))
			fname = dbURL.substring(6).replace('.', '_').replace('/', '_').replace('\\', '_').replace(':', '_').replace(' ', '_');
		else
			fname = dbURL.replace('.', '_').replace('/', '_').replace('\\', '_').replace(':', '_').replace(' ', '_');
		fname = fname.replace("__", "_");
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MMMMM_dd_hh_mm_aaa");
		String timestamp = sdf.format(cal.getTime());
		return "relbackup_" + fname + "_" + timestamp + ".d";
	}

	public static BackupResponse backupToFile(String dbURL, File outputFile) {
		String backupScript;
		try {
			File backupScriptFile = new File("Scripts/DatabaseToScript.d");
			BufferedReader f = new BufferedReader(new FileReader(backupScriptFile));
			StringBuffer fileImage = new StringBuffer();
			String line;
			while ((line = f.readLine()) != null) {
				fileImage.append(line);
				fileImage.append('\n');
			}
			f.close();
			backupScript = fileImage.toString();
		} catch (IOException ioe) {
			return new BackupResponse(ioe.toString(), "Unable to load backup script", javax.swing.JOptionPane.ERROR_MESSAGE, false);
		}
		BufferedWriter outf;
		try {
			outf = new BufferedWriter(new FileWriter(outputFile));
		} catch (IOException ioe) {
			return new BackupResponse(ioe.toString(), "Unable to open output file", javax.swing.JOptionPane.ERROR_MESSAGE, false);			
		}
		boolean receivedOk = false;
		long linesWritten = 0;
		StringReceiverClient client = null;
		try {
			client = ClientFromURL.openConnection(dbURL);
			StringBuffer initialServerResponse = new StringBuffer();
			String r;
			while ((r = client.receive()) != null) {
				initialServerResponse.append(r);
			}
			client.sendExecute(backupScript, new CrashTrap(backupScript, initialServerResponse.toString(), Version.getVersion()));
			try {
				while ((r = client.receive()) != null) {
					if (r.equals("\n")) {
						continue;
					} else if (r.equals("Ok.")) {
						receivedOk = true;
						break;
					} else if (r.startsWith("ERROR:")) {
						outf.close();
						client.close();
						return new BackupResponse(r, "Error executing backup script", javax.swing.JOptionPane.ERROR_MESSAGE, false);
					} else if (r.startsWith("NOTICE")) {
					} else {
						linesWritten++;
						outf.write(r);
						outf.newLine();
					}
				}
			} catch (IOException ioe) {
				outf.close();
				client.close();
				return new BackupResponse(ioe.toString(), "Error executing backup script", javax.swing.JOptionPane.ERROR_MESSAGE, false);
			}			
			client.close();
		} catch (Throwable t) {
			try {
				outf.close();
				if (client != null)
					client.close();
			} catch (IOException e) {
				return new BackupResponse(e.toString(), "Error closing output file", javax.swing.JOptionPane.ERROR_MESSAGE, false);
			}
			return new BackupResponse(t.toString(), "Unable to open database for backup", javax.swing.JOptionPane.ERROR_MESSAGE, false);
		}
		try {
			outf.close();
		} catch (IOException ioe) {
			return new BackupResponse(ioe.toString(), "Unable to close output file", javax.swing.JOptionPane.ERROR_MESSAGE, false);			
		}
		if (receivedOk) {
			if (linesWritten == 0)
				return new BackupResponse("No data was written to the backup.", "Backup failed", javax.swing.JOptionPane.ERROR_MESSAGE, false);
			else {
				BufferedReader inf = null;
				long linesRead = 0;
				try {
					inf = new BufferedReader(new FileReader(outputFile));
					while (inf.readLine() != null)
						linesRead++;
				} catch (IOException ioe) {
					try {
						if (inf != null)
							inf.close();
					} catch (IOException ioe2) {
						return new BackupResponse(ioe2.toString(), "Unable to close backup file after reading it", javax.swing.JOptionPane.ERROR_MESSAGE, false);			
					}
					return new BackupResponse(ioe.toString(), "Unable to read backup file", javax.swing.JOptionPane.ERROR_MESSAGE, false);
				}
				try {
					if (inf != null)
						inf.close();
				} catch (IOException ioe2) {
					return new BackupResponse(ioe2.toString(), "Unable to close backup file after reading it", javax.swing.JOptionPane.ERROR_MESSAGE, false);
				}
				if (linesWritten == linesRead)				
					return new BackupResponse("Backup Successful!  " + linesWritten + " lines were written to\n" + outputFile + "\n\nHowever, you should examine the file to make sure it is complete.", "Backup Complete", javax.swing.JOptionPane.INFORMATION_MESSAGE, true);
				else
					return new BackupResponse("Backup Failed:  " + linesWritten + " lines were written to\n" + outputFile + "\nbut only " + linesRead + " could be read back.", "Backup Failed", javax.swing.JOptionPane.ERROR_MESSAGE, false);
			}
		} else
			return new BackupResponse("The backup may be incomplete.  Please examine the backup!", "Backup Incomplete", javax.swing.JOptionPane.ERROR_MESSAGE, false);
	}

}