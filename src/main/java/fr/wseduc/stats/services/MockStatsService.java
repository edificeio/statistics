package fr.wseduc.stats.services;

import java.util.HashMap;
import java.util.Map;

import static fr.wseduc.webutils.Utils.getOrElse;

import java.io.File;

import fr.wseduc.webutils.Either;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class MockStatsService implements StatsService {

    private static final Logger log = LoggerFactory.getLogger(MockStatsService.class);

    private StructureService structureService;

    private Map<String, JsonArray> baseResponses = new HashMap<>();

    public MockStatsService(Vertx vertx, String path) {
        readFSMocksJson(vertx, path);
    }

    private void readFSMocksJson(Vertx vertx, String path) {
        try {
            FileSystem fs = vertx.fileSystem();
            fs.readDir(path, result -> {
                if (result.succeeded()) {
                    for (String filePath : result.result()) {
                        final Buffer buffer = vertx.fileSystem().readFileBlocking(filePath);
                        final JsonArray json = buffer.toJsonArray();
                        baseResponses.put(filePath.substring(filePath.lastIndexOf(File.separator) + 1, filePath.lastIndexOf(".")), json);
                    }
                } else {
                    log.error("Error reading directory: " + path, result.cause());
                }
            });
        } catch (Exception e) {
            log.error("Error loading mock data", e);
        }
    }

    @Override
	public void listStats(MultiMap params, Handler<Either<String, JsonArray>> handler) {
        final String key = params.get("indicator") + "_" + params.get("frequency") + "_structure" + ("true".equals(params.get("device")) ? "_device" : "");
        final JsonArray mock = getOrElse(baseResponses.get(key), new JsonArray());

        handler.handle(new Either.Right<>(mock));
    }

    @Override
	public void listStatsExport(MultiMap params, String language, Handler<Either<String, JsonArray>> handler) {
        listStats(params, handler);
    }

}
