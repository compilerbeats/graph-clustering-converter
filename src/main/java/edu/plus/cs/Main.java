package edu.plus.cs;

import edu.plus.cs.graph.InteractionGraph;
import edu.plus.cs.interaction.Interaction;
import edu.plus.cs.interaction.io.InteractionFileReader;
import edu.plus.cs.metis.io.MetisFileWriter;
import edu.plus.cs.util.UserIdMappingFileWriter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

public class Main {
    public static void main(String[] args) throws IOException {
        // File file = new File("src/main/resources/test_graph_small.csv");
        File file = new File("src/main/resources/graph_edges_total_45mil_new.csv");

        InteractionFileReader interactionFileReader = new InteractionFileReader(file);
        InteractionGraph interactionGraph = new InteractionGraph();

        // create graph from input file and save the id mapping
        Optional<Interaction> optionalInteraction = interactionFileReader.readLineAsInteraction();
        while (optionalInteraction.isPresent()) {
            interactionGraph.insertInteractionEdge(optionalInteraction.get());

            optionalInteraction = interactionFileReader.readLineAsInteraction();
        }

        HashMap<Long, Long> userIdMapping = interactionGraph.getUserVertexIdMapping();

        MetisFileWriter metisFileWriter = new MetisFileWriter(new File("src/main/resources/output.metis"),
                interactionGraph);
        UserIdMappingFileWriter userIdMappingFileWriter =
                new UserIdMappingFileWriter(new File("src/main/resources/output-user-id-mapping.csv"));

        // convert data to metis format and write the metis + mapping files
        for (long userId : interactionGraph.getAdjacencyLists().keySet()) {
            metisFileWriter.writeUserVertexPair(interactionGraph.getAdjacencyLists().get(userId), userIdMapping);
            userIdMappingFileWriter.writeUserIdMapping(userId, userIdMapping.get(userId));
        }

        metisFileWriter.close();
        userIdMappingFileWriter.close();
    }
}