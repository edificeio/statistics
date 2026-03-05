package fr.wseduc.stats.controllers;

import fr.wseduc.rs.Post;
import fr.wseduc.stats.cron.CronAggregationTask;
import fr.wseduc.webutils.http.BaseController;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class TaskController extends BaseController {
	protected static final Logger log = LoggerFactory.getLogger(TaskController.class);

	final CronAggregationTask cronAggregationTask;

	public TaskController(CronAggregationTask cronAggregationTask) {
		this.cronAggregationTask = cronAggregationTask;
	}

	@Post("api/internal/aggregate")
	public void aggregate(HttpServerRequest request) {
		log.info("Triggered aggregation task");
		cronAggregationTask.handle(0L);
		render(request, null, 202);
	}
}

