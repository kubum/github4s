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

package github4s

import cats.data.{EitherT, Kleisli}
import cats.MonadError
import cats.implicits._
import github4s.GithubResponses._
import github4s.free.interpreters.Interpreters

import scala.concurrent.Future

/**
 * Represent the Github API wrapper
 * @param accessToken to identify the authenticated user
 */
class Github(accessToken: Option[String] = None) {

  lazy val users         = new GHUsers(accessToken)
  lazy val repos         = new GHRepos(accessToken)
  lazy val auth          = new GHAuth
  lazy val gists         = new GHGists(accessToken)
  lazy val issues        = new GHIssues(accessToken)
  lazy val activities    = new GHActivities(accessToken)
  lazy val gitData       = new GHGitData(accessToken)
  lazy val pullRequests  = new GHPullRequests(accessToken)
  lazy val organizations = new GHOrganizations(accessToken)

}

/** Companion object for [[github4s.Github]] */
object Github {
  def apply(accessToken: Option[String] = None) = new Github(accessToken)

  implicit class GithubIOSyntaxEither[A](gio: GHIO[GHResponse[A]]) {

    def execK[M[_]](
        implicit I: Interpreters[M],
        A: MonadError[M, Throwable]): Kleisli[M, Map[String, String], GHResponse[A]] =
      gio foldMap I.interpreters

    def exec[M[_]](headers: Map[String, String] = Map())(
        implicit I: Interpreters[M],
        A: MonadError[M, Throwable]): M[GHResponse[A]] =
      execK.run(headers)

    def execFuture(headers: Map[String, String] = Map())(
        implicit I: Interpreters[Future],
        A: MonadError[Future, Throwable]): Future[GHResponse[A]] =
      exec[Future](headers)

    def liftGH: EitherT[GHIO, GHException, GHResult[A]] =
      EitherT[GHIO, GHException, GHResult[A]](gio)

  }
}
