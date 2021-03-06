/*
 * Accio is a platform to launch computer science experiments.
 * Copyright (C) 2016-2018 Vincent Primault <v.primault@ucl.ac.uk>
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

namespace java fr.cnrs.liris.lumos.domain.thrift

enum DataType {
  INT,
  LONG,
  FLOAT,
  DOUBLE,
  STRING,
  BOOLEAN,
  BLOB,
  DATASET,
}

struct RemoteFile {
  1: string uri;
  2: string content_type;
  3: optional string format;
  4: optional i64 size_kb;
  5: optional string sha256;
}

union Value {
  1: i32 int;
  2: i64 long;
  3: double float;
  4: double dbl;
  5: string str;
  6: bool boolean;
  7: RemoteFile dataset;
  8: RemoteFile file;
}

struct MetricValue {
  1: string name;
  2: double value;
  3: set<string> aspects;
}

struct AttrValue {
  1: string name;
  2: DataType data_type;
  3: Value value;
  4: set<string> aspects;
}

enum ExecState {
  PENDING,
  RUNNING,
  SUCCESSFUL,
  FAILED,
  CANCELED,
}

struct ExecStatus {
  1: ExecState state;
  2: i64 time;
  3: optional string message;
}

struct Link {
  1: string title;
  2: string url;
}

struct ErrorDatum {
  1: string mnemonic;
  2: optional string message;
  3: list<string> stacktrace;
}

struct Task {
  1: string name;
  2: optional string mnemonic;
  3: set<string> dependencies;
  4: list<Link> links;
  5: map<string, string> metadata;
  6: optional i32 exit_code;
  7: list<MetricValue> metrics;
  8: optional ErrorDatum error;
  9: optional ExecStatus status;
  10: list<ExecStatus> history;
}

struct Job {
  1: string name;
  2: i64 create_time;
  3: optional string owner;
  4: optional string contact;
  5: map<string, string> labels;
  6: map<string, string> metadata;
  7: list<AttrValue> inputs;
  8: list<AttrValue> outputs;
  9: i32 progress;
  10: list<Task> tasks;
  11: optional ExecStatus status;
  12: list<ExecStatus> history;
}

struct JobEnqueuedEvent {
  1: Job job;
}

struct JobExpandedEvent {
  1: list<Task> tasks;
}

struct JobStartedEvent {
  1: map<string, string> metadata;
  2: optional string message;
}

struct JobCompletedEvent {
  1: list<AttrValue> outputs;
  2: optional string message;
}

struct JobCanceledEvent {
  1: optional string message;
}

struct TaskStartedEvent {
  1: string name;
  2: list<Link> links;
  3: map<string, string> metadata;
  4: optional string message;
}

struct TaskCompletedEvent {
  1: string name;
  2: i32 exit_code;
  3: list<MetricValue> metrics;
  4: optional ErrorDatum error;
  5: optional string message;
}

union EventPayload {
  1: JobEnqueuedEvent job_enqueued;
  2: JobExpandedEvent job_expanded;
  3: JobStartedEvent job_started;
  4: JobCompletedEvent job_completed;
  5: JobCanceledEvent job_canceled;
  6: TaskStartedEvent task_started;
  7: TaskCompletedEvent task_completed;
}

struct Event {
  1: string parent;
  2: i64 sequence;
  3: i64 time;
  4: EventPayload payload;
}