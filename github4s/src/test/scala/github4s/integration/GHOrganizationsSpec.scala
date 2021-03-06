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

import github4s.Github
import github4s.Github._
import github4s.free.domain.User
import github4s.implicits1._
import github4s.utils.{BaseIntegrationSpec, Integration}

trait GHOrganizationsSpec extends BaseIntegrationSpec {

  "Organization >> ListMembers" should "return the expected list of users" taggedAs Integration in {
    val response =
      Github(accessToken).organizations
        .listMembers(validRepoOwner)
        .execFuture(headerUserAgent)

    testFutureIsRight[List[User]](response, { r =>
      r.result.nonEmpty shouldBe true
      r.statusCode shouldBe okStatusCode
    })
  }

  it should "return error for an invalid org" taggedAs Integration in {
    val response =
      Github(accessToken).organizations
        .listMembers(invalidUsername)
        .execFuture(headerUserAgent)

    testFutureIsLeft(response)
  }

  "Organization >> ListOutsideCollaborators" should "return expected list of users" taggedAs Integration in {
    val response =
      Github(accessToken).organizations
        .listOutsideCollaborators(validOrganizationName)
        .execFuture(headerUserAgent)

    testFutureIsRight[List[User]](response, { r =>
      r.result.nonEmpty shouldBe true
      r.statusCode shouldBe okStatusCode
    })
  }

  it should "return error for an invalid org" taggedAs Integration in {
    val response =
      Github(accessToken).organizations
        .listOutsideCollaborators(invalidOrganizationName)
        .execFuture(headerUserAgent)

    testFutureIsLeft(response)
  }

}
