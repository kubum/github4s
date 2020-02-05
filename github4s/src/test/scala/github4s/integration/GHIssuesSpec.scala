/*
 * Copyright 2016-2020 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package github4s.integration

import cats.effect.IO
import github4s.GithubIOSyntax._
import github4s.Github
import github4s.domain.{Issue, Label, SearchIssuesResult, User}
import github4s.utils.{BaseIntegrationSpec, Integration}

trait GHIssuesSpec extends BaseIntegrationSpec {

  "Issues >> List" should "return a list of issues" taggedAs Integration in {
    val response = Github[IO](accessToken).issues
      .listIssues(validRepoOwner, validRepoName, headerUserAgent)
      .toFuture

    testFutureIsRight[List[Issue]](response, { r =>
      r.result.nonEmpty shouldBe true
      r.statusCode shouldBe okStatusCode
    })
  }

  "Issues >> Get" should "return an issue which is a PR" taggedAs Integration in {
    val response = Github[IO](accessToken).issues
      .getIssue(validRepoOwner, validRepoName, validPullRequestNumber, headerUserAgent)
      .toFuture

    testFutureIsRight[Issue](response, { r =>
      r.result.pull_request.isDefined shouldBe true
      r.statusCode shouldBe okStatusCode
    })
  }

  "Issues >> Search" should "return at least one issue for a valid query" taggedAs Integration in {
    val response = Github[IO](accessToken).issues
      .searchIssues(validSearchQuery, validSearchParams, headerUserAgent)
      .toFuture

    testFutureIsRight[SearchIssuesResult](response, { r =>
      r.result.total_count > 0 shouldBe true
      r.result.items.nonEmpty shouldBe true
      r.statusCode shouldBe okStatusCode
    })
  }

  it should "return an empty result for a non existent query string" taggedAs Integration in {
    val response = Github[IO](accessToken).issues
      .searchIssues(nonExistentSearchQuery, validSearchParams, headerUserAgent)
      .toFuture

    testFutureIsRight[SearchIssuesResult](response, { r =>
      r.result.total_count shouldBe 0
      r.result.items.nonEmpty shouldBe false
      r.statusCode shouldBe okStatusCode
    })
  }

  "Issues >> Edit" should "edit the specified issue" taggedAs Integration in {
    val response = Github[IO](accessToken).issues
      .editIssue(
        validRepoOwner,
        validRepoName,
        validIssueNumber,
        validIssueState,
        validIssueTitle,
        validIssueBody,
        None,
        validIssueLabel,
        validAssignees,
        headerUserAgent)
      .toFuture

    testFutureIsRight[Issue](response, { r =>
      r.result.state shouldBe validIssueState
      r.result.title shouldBe validIssueTitle
      r.statusCode shouldBe okStatusCode
    })
  }

  "Issues >> ListLabels" should "return a list of labels" taggedAs Integration in {
    val response = Github[IO](accessToken).issues
      .listLabels(validRepoOwner, validRepoName, validIssueNumber, headerUserAgent)
      .toFuture

    testFutureIsRight[List[Label]](response, { r =>
      r.result.nonEmpty shouldBe true
      r.statusCode shouldBe okStatusCode
    })
  }

  "Issues >> RemoveLabel" should "return a list of removed labels" taggedAs Integration in {
    val response = Github[IO](accessToken).issues
      .removeLabel(
        validRepoOwner,
        validRepoName,
        validIssueNumber,
        validIssueLabel.head,
        headerUserAgent)
      .toFuture

    testFutureIsRight[List[Label]](response, { r =>
      r.result.nonEmpty shouldBe true
      r.statusCode shouldBe okStatusCode
    })
  }

  "Issues >> AddLabels" should "return a list of labels" taggedAs Integration in {
    val response = Github[IO](accessToken).issues
      .addLabels(validRepoOwner, validRepoName, validIssueNumber, validIssueLabel, headerUserAgent)
      .toFuture

    testFutureIsRight[List[Label]](response, { r =>
      r.result.nonEmpty shouldBe true
      r.statusCode shouldBe okStatusCode
    })
  }

  "GHIssues >> ListAvailableAssignees" should "return a list of users" taggedAs Integration in {
    val response = Github[IO](accessToken).issues
      .listAvailableAssignees(validRepoOwner, validRepoName, None, headerUserAgent)
      .toFuture

    testFutureIsRight[List[User]](response, { r =>
      r.result.nonEmpty shouldBe true
      r.statusCode shouldBe okStatusCode
    })
  }

  it should "return error for an invalid repo owner" taggedAs Integration in {
    val response = Github[IO](accessToken).issues
      .listAvailableAssignees(invalidRepoOwner, validRepoName, None, headerUserAgent)
      .toFuture

    testFutureIsLeft(response)
  }

  it should "return error for an invalid repo name" taggedAs Integration in {
    val response = Github[IO](accessToken).issues
      .listAvailableAssignees(validRepoOwner, invalidRepoName, None, headerUserAgent)
      .toFuture

    testFutureIsLeft(response)
  }

}
