/*
 * Accio is a platform to launch computer science experiments.
 * Copyright (C) 2016-201 8 Vincent Primault <v.primault@ucl.ac.uk>
 *
 * Accio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Accio is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Accio.  If not, see <http://www.gnu.org/licenses/>.
 */

namespace java fr.cnrs.liris.lumos.server

include "accio/thrift/fr/cnrs/liris/lumos/domain/lumos.thrift"

enum ErrorCode {
  ALREADY_EXISTS,
  NOT_FOUND,
  FAILED_PRECONDITION,
  INVALID_ARGUMENT,
  UNAUTHENTICATED,
  UNIMPLEMENTED,
}

exception ServerException {
  1: ErrorCode code;
  2: optional string message;
  3: optional string resource_type;
  4: optional string resource_name;
  5: optional list<string> errors;
}

struct GetInfoRequest {
}

struct GetInfoResponse {
  1: required string version;
}

struct PushEventRequest {
  1: lumos.Event event;
}

struct PushEventResponse {
}

struct GetJobRequest {
  1: required string name;
}

struct GetJobResponse {
  1: required lumos.Job job;
}

struct ListJobsRequest {
  1: optional string labels;
  2: optional string owner;
  3: optional set<lumos.ExecState> state;
  10: optional i32 limit;
  11: optional i32 offset;
}

struct ListJobsResponse {
  1: required list<lumos.Job> jobs;
  2: required i64 total_count;
}

service LumosService {
  // Get information about this server.
  GetInfoResponse getInfo(1: GetInfoRequest req) throws (1: ServerException e);

  // Push an event.
  PushEventResponse pushEvent(1: PushEventRequest req) throws (1: ServerException e);

  // Retrieve a specific job.
  GetJobResponse getJob(1: GetJobRequest req) throws (1: ServerException e);

  // Retrieve all jobs matching some criteria.
  ListJobsResponse listJobs(1: ListJobsRequest req) throws (1: ServerException e);
}
