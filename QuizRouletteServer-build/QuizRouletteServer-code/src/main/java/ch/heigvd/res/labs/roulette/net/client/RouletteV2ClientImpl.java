package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import javax.imageio.IIOException;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {
    
    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(RouletteV2ClientImpl.class.getName());
  
    
    
  @Override
  public void clearDataStore() throws IOException {
      writer.println(RouletteV2Protocol.CMD_CLEAR);
      writer.flush();

      if(!lineReader().equalsIgnoreCase(RouletteV2Protocol.RESPONSE_CLEAR_DONE)){
          throw new IOException("server response not correct....");
      }
      
  }

  @Override
  public List<Student> listStudents() throws IOException {
      writer.println(RouletteV2Protocol.CMD_LIST);
      writer.flush();
      String line = lineReader();
      StudentsList s = JsonObjectMapper.parseJson(line, StudentsList.class);
      return s.getStudents();
  }
  
  
    @Override
  public void loadStudents(List<Student> students) throws IOException {
      writer.println(RouletteV1Protocol.CMD_LOAD);
      writer.flush();
      
      if(!lineReader().equalsIgnoreCase(RouletteV1Protocol.RESPONSE_LOAD_START)){
          throw new IOException("LOAD START server response not correct....");
      }
      Iterator<Student> s = students.iterator();
      while(s.hasNext()){
          writer.println(s.next().getFullname());
          writer.flush();
      }
      writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      writer.flush();

      LoadCommandResponse lcs = JsonObjectMapper.parseJson(lineReader(), LoadCommandResponse.class);
      
      if (lcs.getStatus().equalsIgnoreCase("success")) {
          LOG.log(Level.INFO, "Added successfully: {0} students", lcs.getNumberOfStudents());
      } else {
          LOG.severe("Error. Students not added...");
      } 
      if(!lineReader().equalsIgnoreCase(RouletteV1Protocol.RESPONSE_LOAD_DONE)){
          throw new IOException("V2 LOAD DONE server response not correct....");
      }
  }

}
