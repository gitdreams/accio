/*
 * Accio is a program whose purpose is to study location privacy.
 * Copyright (C) 2016-2017 Vincent Primault <vincent.primault@liris.cnrs.fr>
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

import React from 'react'
import {Grid} from 'react-bootstrap'
import Spinner from 'react-spinkit'
import WorkflowEdit from './WorkflowEdit'
import xhr from '../../../utils/xhr'

class WorkflowEditContainer extends React.Component {
  constructor(props) {
    super(props)
    this.state = {data: null}
  }

  _loadData(props) {
    const url = '/api/v1/workflow/' + props.params.id
    xhr(url).then(data => this.setState({data}))
  }

  componentWillReceiveProps(nextProps) {
    if (this.props.params.id !== nextProps.params.id) {
      this._loadData(nextProps)
    }
  }

  componentDidMount() {
    this._loadData(this.props)
  }

  render() {
    return (null !== this.state.data)
      ? <WorkflowEdit workflow={this.state.data} {...this.props}/>
      : <Grid><Spinner spinnerName="three-bounce"/></Grid>
  }
}

WorkflowEditContainer.propTypes = {
  params: React.PropTypes.shape({
    id: React.PropTypes.string.isRequired,
  })
}

export default WorkflowEditContainer