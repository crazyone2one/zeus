
const en_US = {
    home: {
        table: {
            index: "Index",
            task_type: "Task Type",
            run_rule: "Rule",
            task_status: "Status",
            next_execution_time: "Next Execution Time",
            create_user: "Creator",
            update_time: "Update time",
        },
        case: {
            index: "Ranking",
            case_name: "Case Name",
            case_type: "Case Type",
            test_plan: "Test Plan",
            failure_times: "Failure times",
        },
        rate: {
            case_review: "Review rate",
            case_review_pass: "Review pass rate",
            cover: "Cover rate",
            legacy: "Legacy rate",
            legacy_issue: "Percentage of legacy defects",
        },
        dashboard: {
            public: {
                this_week: "Week ",
                load_error: "Loading failure",
                no_data: "No data",
                no_search_data: "No search data",
            },
            case_finished_review_pass_tip: "Reviewed cases/All reviewed cases *100%",
        },
        case_review_dashboard: {
            case_count: "Case",
            not_review: "Not reviewed",
            finished_review: "Reviewed",
            not_pass: "Not pass",
            pass: "Pass",
        },
        relevance_dashboard: {
            api_case: "Api case",
            scenario_case: "Scenario case",
            performance_case: "Performance case",
            relevance_case_count: "Relevance case",
            not_cover: "Not cover",
            cover: "Cover",
        },
        bug_dashboard: {
            un_closed_bug_count: "Unclosed bug",
            un_closed_range: "Unclosed bug rate",
            un_closed_range_tips: "Unclosed bugs/all associated bugs *100%",
            un_closed_bug_case_range: "Unclosed bug case rate",
            un_closed_bug_case_range_tips: "Unclosed bugs/all associated cases *100%",
            un_closed_count: "Unclosed bug",
            total_count: "All related bug",
            case_count: "Case count",
        },
    },
    plan: {
        error_samples: "Error samples",
        all_samples: "All samples",
        response_3_samples: "The first three pieces of data",
        batch_delete_tip: "Do you want to continue deleting the test plan?",
        relevance_case_success: "Relevance success",
    },
    review: {
        result_distribution: "Result Distribution",
        review_pass_rule: "Review Pass Criteria",
        review_pass_rule_all: "All Pass",
        review_pass_rule_single: "Single Pass",
        update_review_reviewer_tip:
            "Note: Modifying the reviewer will overwrite the reviewers associated with the use case, and update the review status of the use case, Please exercise caution!",
        review_rule_tip:
            "All pass: Pass only if all reviewers pass </br> Single pass: Pass if any reviewers pass",
        update_review_rule_tip:
            "Note: Modifying the standard will affect the reviewed use cases, please exercise caution!",
        review_history: "Review History",
        no_review_history: "There is no review history",
        added_comment: "Added comment",
        un_pass_review_confirm: "Are you sure you don't pass this review",
        please_input_review_comment: "Please enter the review comments",
        pass_review_confirm: "Are you sure to pass this review",
        comment_require: "(require)",
        comment_not_require: "(not_require)",
        search_by_id_or_name_or_tag: "Search by ID/Name/Tag",
    },
    case: {
        all_case_content: "All case",
        use_case_detail: "Use Case Details",
        associate_test_cases: "Associate Test Cases",
        dependencies: "Dependence",
        comment: "Comment",
        change_record: "Change record",
        case_name: "Case Name",
        please_enter_the_case_name: "Please enter the case name",
        preconditions: "Preconditions",
        please_enter_preconditions: "Please enter preconditions",
        attachment: "Attachment",
        none: "None",
        commented: "Commented",
        add_attachment: "Add Attachment",
        file_size_limit:
            "Any type of file is supported, and the file size does not exceed 50MB",
        file_size_out_of_bounds: "File size does not exceed 50MB",
        upload_at: "upload",
        relate_at: "relate",
        add_steps: "Add Steps",
        insert_steps: "Insert Steps",
        copy_this_step: "Copy step",
        more: "More",
        follow: "Follow",
        followed: "Followed",
        previous_public_case: "Previous",
        next_public_case: "Next",
        add_to_public_case: "Add to Common Use Case Library",
        added_to_public_case: "Add to public",
        make_comment: "Make comment",
        please_enter_comment: "Please enter a comment",
        associated_defect: "Associated",
        create_defect: "Create",
        associate_existing_defects: "Associate",
        search_by_id: "Search by ID or Name",
        relieve: "Relieve",
        content_before_change: "Content before change",
        content_after_change: "Content after change",
        empty_tip: "Empty",
        all_case: "All case",
        all_scenes: "All scenes",
        all_api: "All interfaces",
        associated_files: "Associated",
        empty_file: "No files",
        upload_file: "Upload files",
        selected: "Selected",
        strip: "Strip",
        clear: "Clear",
        please_enter_a_text_description: "Please enter a text description",
        please_enter_expected_results: "Please enter expected results",
        please_enter_comments: "Please enter comments",
        disassociate: "Disassociate",
        saveAndCreate: "Save and continue create",
        last_version: "Last version",
        set_new: "Set new",
        version_comparison: "Version comparison",
        compare: "Compare",
        project: "Project",
        create_version: "New Version",
        choose_copy_info: "Select replication information",
        current_display_history_version: "Current display history version",
        compare_with_the_latest_version: "Compare with the latest version",
        view_the_latest_version: "View the latest version",
        version_id_cannot_be_empty: "Version id cannot be empty",
        enter_comments_and_click_send: "Enter comments and click Send",
        cancel_relate_case_tips_title: "Confirm cancel?",
        cancel_relate_case_tips_content:
            "Cancel it will affect the test plan statistics. Sure?",
        back_tips: "TestCase is not saved, Are you sure to quit?",
        dependency_remove_confirm: "Is dependency release confirmed?",
        minder_paste_tip:
            "The pasted node has modules with unloaded use cases, copying unloaded use cases is not currently supported!",
        minder_move_confirm_tip:
            "There is currently field sorting, and the use case order cannot be set. Please switch to the use case list and cancel the field sorting!",
        minder_module_move_confirm_tip: "Module does not support setting order!",
        public: {
            remove: "Whether to remove the use case ",
            batch_remove_confirm: "Confirm remove {0} item use case?",
        },
        enter_issues_content: "Please enter the defect content...",
    },
    attachment: {
        preview: "Preview",
        download: "Download",
        dump: "Dump",
        unRelate: "Unlink",
        delete: "Delete",
        delete_confirm_tips: "Delete This Item ?",
        upload_success: "Upload success",
        upload_error: "Upload error",
        not_exits: "File not exits",
        waiting_upload: "Waiting upload",
        waiting_relate: "Waiting relate",
    },
}

export default en_US
